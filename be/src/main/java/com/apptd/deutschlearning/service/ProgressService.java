package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.dto.progress.LessonProgressRowDto;
import com.apptd.deutschlearning.dto.progress.SubmitProgressRequest;
import com.apptd.deutschlearning.dto.progress.SubmitProgressResponse;
import com.apptd.deutschlearning.dto.progress.UserProgressSummaryDto;
import com.apptd.deutschlearning.entity.LessonEntity;
import com.apptd.deutschlearning.entity.UserEntity;
import com.apptd.deutschlearning.entity.UserProgressEntity;
import com.apptd.deutschlearning.entity.enums.ProgressStatus;
import com.apptd.deutschlearning.exception.NotFoundException;
import com.apptd.deutschlearning.repository.LessonRepository;
import com.apptd.deutschlearning.repository.UserProgressRepository;
import com.apptd.deutschlearning.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class ProgressService {
  private final UserRepository userRepository;
  private final LessonRepository lessonRepository;
  private final UserProgressRepository userProgressRepository;
  private final ScoringService scoringService;

  public ProgressService(
      UserRepository userRepository,
      LessonRepository lessonRepository,
      UserProgressRepository userProgressRepository,
      ScoringService scoringService
  ) {
    this.userRepository = userRepository;
    this.lessonRepository = lessonRepository;
    this.userProgressRepository = userProgressRepository;
    this.scoringService = scoringService;
  }

  @Transactional(readOnly = true)
  public UserProgressSummaryDto getMyProgress(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "Không tìm thấy user id=" + userId));

    List<UserProgressEntity> rows = userProgressRepository.findAllByUserIdWithLesson(userId);
    Comparator<UserProgressEntity> byOrder = Comparator
        .comparing((UserProgressEntity up) -> up.getLesson().getMenschenOrder() == null ? Integer.MAX_VALUE : up.getLesson().getMenschenOrder())
        .thenComparing(up -> up.getLesson().getId());

    List<LessonProgressRowDto> lessons = rows.stream()
        .sorted(byOrder)
        .map(up -> new LessonProgressRowDto(
            up.getLesson().getId(),
            up.getLesson().getMenschenOrder(),
            up.getLesson().getTitle(),
            up.getLesson().getLevel().name(),
            up.getStatus().name(),
            up.getBestScorePercent(),
            up.getLastScorePercent(),
            up.getAttempts(),
            up.getXpAwardedTotal(),
            up.getCompletedAt()
        ))
        .toList();

    return new UserProgressSummaryDto(user.getUsername(), user.getTotalXp(), lessons);
  }

  /**
   * Ghi nhận kết quả sau khi học sinh làm bài trong lesson (FE gọi tự động), không dùng form nhập tay.
   */
  @Transactional
  public SubmitProgressResponse submitProgress(Long userId, SubmitProgressRequest request) {
    LessonEntity lesson = lessonRepository.findById(request.lessonId())
        .orElseThrow(() -> new NotFoundException("LESSON_NOT_FOUND", "Không tìm thấy bài học id=" + request.lessonId()));

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "Không tìm thấy user id=" + userId));

    UserProgressEntity progress = userProgressRepository.findByUser_IdAndLesson_Id(userId, lesson.getId())
        .orElseGet(() -> UserProgressEntity.builder()
            .user(user)
            .lesson(lesson)
            .status(ProgressStatus.NOT_COMPLETED)
            .lastScorePercent(0)
            .bestScorePercent(0)
            .attempts(0)
            .xpAwardedTotal(0)
            .completedAt(null)
            .build());

    int nextAttempts = progress.getAttempts() + 1;
    progress.setAttempts(nextAttempts);
    progress.setLastScorePercent(request.scorePercent());
    int newBest = Math.max(progress.getBestScorePercent(), request.scorePercent());
    progress.setBestScorePercent(newBest);

    // XP theo điểm tốt nhất, trần = xp_reward bài học; lần này chỉ cộng phần chưa nhận.
    long previousLessonXp = progress.getXpAwardedTotal();
    long newDeserved = scoringService.deservedTotalXpForLesson(lesson, newBest);
    long deltaXp = newDeserved - previousLessonXp;
    progress.setXpAwardedTotal(newDeserved);

    if (request.scorePercent() == 100) {
      progress.setStatus(ProgressStatus.COMPLETED);
      progress.setCompletedAt(Instant.now());
    } else {
      progress.setStatus(ProgressStatus.NOT_COMPLETED);
    }

    user.setTotalXp(Math.max(0L, user.getTotalXp() + deltaXp));

    userProgressRepository.save(progress);
    userRepository.save(user);

    // xpAwarded = phần cộng thêm lần này (FE dùng confetti); bonusXp giữ field, luôn 0.
    return new SubmitProgressResponse(
        lesson.getId(),
        request.scorePercent(),
        deltaXp,
        0L,
        user.getTotalXp(),
        progress.getLastScorePercent(),
        progress.getBestScorePercent(),
        progress.getAttempts()
    );
  }
}
