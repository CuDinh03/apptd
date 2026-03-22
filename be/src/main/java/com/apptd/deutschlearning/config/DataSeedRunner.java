package com.apptd.deutschlearning.config;

import com.apptd.deutschlearning.entity.*;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.entity.enums.Role;
import com.apptd.deutschlearning.config.seed.MenschenA1CurriculumSeed;
import com.apptd.deutschlearning.config.seed.MenschenA1CurriculumSeed.ExerciseSpec;
import com.apptd.deutschlearning.config.seed.MenschenA1CurriculumSeed.LessonSpec;
import com.apptd.deutschlearning.config.seed.MenschenA1CurriculumSeed.ExamplePhraseSpec;
import com.apptd.deutschlearning.config.seed.MenschenA1CurriculumSeed.VocabSpec;
import com.apptd.deutschlearning.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Seed dữ liệu mẫu (P0) — chạy mọi profile (kể cả prod), idempotent.
 * Hỗ trợ ký tự tiếng Đức: ä, ö, ü, ß.
 */
@Component
@Order(Integer.MAX_VALUE)
public class DataSeedRunner implements ApplicationRunner {

  private final UserRepository userRepository;
  private final LessonRepository lessonRepository;
  private final VocabularyRepository vocabularyRepository;
  private final SpeakingTopicRepository speakingTopicRepository;
  private final LessonExerciseRepository lessonExerciseRepository;
  private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
  private final ObjectMapper objectMapper;

  public DataSeedRunner(
      UserRepository userRepository,
      LessonRepository lessonRepository,
      VocabularyRepository vocabularyRepository,
      SpeakingTopicRepository speakingTopicRepository,
      LessonExerciseRepository lessonExerciseRepository,
      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
      ObjectMapper objectMapper
  ) {
    this.userRepository = userRepository;
    this.lessonRepository = lessonRepository;
    this.vocabularyRepository = vocabularyRepository;
    this.speakingTopicRepository = speakingTopicRepository;
    this.lessonExerciseRepository = lessonExerciseRepository;
    this.passwordEncoder = passwordEncoder;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {
    seedUsersIfNeeded();
    seedLessonsIfNeeded();
  }

  /**
   * {@code admin} / Admin12345 (ROLE_ADMIN) và {@code student} / Student123 (USER).
   * Admin: tạo nếu thiếu; nếu đã có thì đảm bảo role ADMIN và mật khẩu đúng theo yêu cầu triển khai.
   * Student: chỉ tạo nếu chưa tồn tại.
   */
  private void seedUsersIfNeeded() {
    upsertAdminUser();
    if (!userRepository.existsByUsername("student")) {
      userRepository.save(UserEntity.builder()
          .username("student")
          .passwordHash(passwordEncoder.encode("Student123"))
          .role(Role.USER)
          .totalXp(0)
          .build());
    }
  }

  private void upsertAdminUser() {
    var existing = userRepository.findByUsername("admin");
    if (existing.isEmpty()) {
      userRepository.save(UserEntity.builder()
          .username("admin")
          .passwordHash(passwordEncoder.encode("Admin12345"))
          .role(Role.ADMIN)
          .totalXp(0)
          .build());
      return;
    }
    UserEntity admin = existing.get();
    boolean dirty = false;
    if (admin.getRole() != Role.ADMIN) {
      admin.setRole(Role.ADMIN);
      dirty = true;
    }
    if (!passwordEncoder.matches("Admin12345", admin.getPasswordHash())) {
      admin.setPasswordHash(passwordEncoder.encode("Admin12345"));
      dirty = true;
    }
    if (dirty) {
      userRepository.save(admin);
    }
  }

  /**
   * Đảm bảo field {@code menschenOrder} đã được set cho các lesson A1 (legacy DB).
   */
  private void ensureMenschenOrdersIfMissing() {
    for (LessonEntity lesson : lessonRepository.findAll()) {
      if (lesson.getMenschenOrder() != null) continue;

      Integer order = switch (lesson.getTitle()) {
        case "Begrüßungen — chào hỏi (A1)" -> 1;
        case "Sich vorstellen — giới thiệu bản thân (A1)" -> 2;
        case "Zahlen & Uhrzeit — con số và giờ (A1)" -> 3;
        case "Familie — gia đình (A1)" -> 4;
        case "Essen & Trinken — ăn uống (A1)" -> 5;
        case "Routinen — thói quen hằng ngày (A1)" -> 6;
        case "Freizeit & Hobbys — thời gian rảnh (A1)" -> 7;
        case "Woche & Monate — tuần và tháng (A1)" -> 8;
        case "Wohnung — nhà ở (A1)" -> 9;
        case "Möbel — đồ nội thất (A1)" -> 10;
        case "Stadt & Weg — thành phố và đường (A1)" -> 11;
        case "Verkehrsmittel — phương tiện (A1)" -> 12;
        case "Einkaufen & Preis — mua sắm (A1)" -> 13;
        case "Kleidung — quần áo (A1)" -> 14;
        case "Farben — màu sắc (A1)" -> 15;
        case "Wetter — thời tiết (A1)" -> 16;
        case "Körper — cơ thể (A1)" -> 17;
        case "Gesundheit — sức khỏe (A1)" -> 18;
        case "Beruf & Arbeit — nghề nghiệp (A1)" -> 19;
        case "Reisen — du lịch (A1)" -> 20;
        case "Feste & Geschenke — lễ và quà (A1)" -> 21;
        case "Höflichkeit — lịch sự (A1)" -> 22;
        case "Perfekt leicht — quá khứ cơ bản (A1)" -> 23;
        case "Wiederholung A1 — ôn tập (A1)" -> 24;
        default -> null;
      };

      if (order != null) {
        lesson.setMenschenOrder(order);
        lessonRepository.save(lesson);
      }
    }
  }

  /**
   * Đồng bộ usage + câu mẫu từ curriculum seed (dev): cập nhật mỗi lần chạy để sửa nội dung seed được phản ánh.
   */
  private void ensureLessonUsageContentIfMissing() throws Exception {
    for (LessonSpec spec : MenschenA1CurriculumSeed.allLessons()) {
      lessonRepository.findByLevelAndMenschenOrder(LessonLevel.A1, spec.menschenOrder()).ifPresent(lesson -> {
        try {
          lesson.setUsageNotesJson(objectMapper.writeValueAsString(spec.usageNotesVi()));
          lesson.setExamplePhrasesJson(examplePhrasesToJson(spec.examplePhrases()));
          lessonRepository.save(lesson);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private String examplePhrasesToJson(List<ExamplePhraseSpec> phrases) throws Exception {
    List<Map<String, String>> rows = phrases.stream()
        .map(p -> Map.of("german", p.german(), "vietnamese", p.vietnamese()))
        .toList();
    return objectMapper.writeValueAsString(rows);
  }

  /**
   * Menschen A1: 24 bài (nội dung tự biên). DB trống → seed hết; đã có dữ liệu → chỉ thêm bài thiếu theo {@code menschenOrder}.
   */
  private void seedLessonsIfNeeded() throws Exception {
    ensureMenschenOrdersIfMissing();
    ensureLessonUsageContentIfMissing();
    List<LessonSpec> specs = MenschenA1CurriculumSeed.allLessons();

    if (lessonRepository.count() == 0) {
      for (LessonSpec spec : specs) {
        persistLesson(spec);
      }
      return;
    }

    for (LessonSpec spec : specs) {
      if (!lessonRepository.existsByLevelAndMenschenOrder(LessonLevel.A1, spec.menschenOrder())) {
        persistLesson(spec);
      }
    }
  }

  private void persistLesson(LessonSpec spec) throws Exception {
    LessonEntity lesson = lessonRepository.save(LessonEntity.builder()
        .title(spec.title())
        .menschenOrder(spec.menschenOrder())
        .level(LessonLevel.A1)
        .category(spec.category())
        .xpReward(50)
        .usageNotesJson(objectMapper.writeValueAsString(spec.usageNotesVi()))
        .examplePhrasesJson(examplePhrasesToJson(spec.examplePhrases()))
        .build());

    for (VocabSpec v : spec.vocabs()) {
      vocabularyRepository.save(VocabularyEntity.builder()
          .lesson(lesson)
          .word(v.word())
          .article(v.article())
          .pluralForm(v.pluralForm())
          .meaningVi(v.meaningVi())
          .pronunciationIpa(v.pronunciationIpa())
          .usageNoteVi(v.usageNoteVi())
          .audioUrl(null)
          .build());
    }

    speakingTopicRepository.save(SpeakingTopicEntity.builder()
        .contextPrompt(spec.speakingPrompt())
        .levelRequirement(LessonLevel.A1)
        .build());

    int sort = 1;
    for (ExerciseSpec ex : spec.exercises()) {
      String choicesJson = (ex.choices() == null || ex.choices().isEmpty())
          ? null
          : objectMapper.writeValueAsString(ex.choices());
      lessonExerciseRepository.save(LessonExerciseEntity.builder()
          .lesson(lesson)
          .exerciseType(ex.type())
          .questionText(ex.questionText())
          .choicesJson(choicesJson)
          .correctAnswer(ex.correctAnswer())
          .sortOrder(sort++)
          .build());
    }
  }
}
