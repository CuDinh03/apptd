package com.apptd.deutschlearning.entity;

import com.apptd.deutschlearning.entity.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "user_progress",
    uniqueConstraints = @UniqueConstraint(name = "uk_user_lesson", columnNames = {"user_id", "lesson_id"})
)
public class UserProgressEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "lesson_id", nullable = false)
  private LessonEntity lesson;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 30)
  private ProgressStatus status;

  // Điểm % đạt được ở lần làm gần nhất
  @Column(name = "last_score_percent", nullable = false)
  private int lastScorePercent;

  // Điểm % tốt nhất của user cho lesson này
  @Column(name = "best_score_percent", nullable = false)
  private int bestScorePercent;

  @Column(name = "attempts", nullable = false)
  private int attempts;

  // XP đã cộng dồn cho lesson này (có thể dùng cho thống kê về sau)
  @Column(name = "xp_awarded_total", nullable = false)
  private long xpAwardedTotal;

  @Column(name = "completed_at")
  private Instant completedAt;
}

