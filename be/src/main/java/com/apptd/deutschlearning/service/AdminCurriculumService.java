package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.dto.admin.*;
import com.apptd.deutschlearning.dto.lesson.ExamplePhraseJsonRow;
import com.apptd.deutschlearning.entity.LessonEntity;
import com.apptd.deutschlearning.entity.LessonExerciseEntity;
import com.apptd.deutschlearning.entity.VocabularyEntity;
import com.apptd.deutschlearning.entity.enums.ExerciseType;
import com.apptd.deutschlearning.entity.enums.LessonCategory;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.exception.BadRequestException;
import com.apptd.deutschlearning.exception.ConflictException;
import com.apptd.deutschlearning.exception.NotFoundException;
import com.apptd.deutschlearning.repository.LessonExerciseRepository;
import com.apptd.deutschlearning.repository.LessonRepository;
import com.apptd.deutschlearning.repository.UserProgressRepository;
import com.apptd.deutschlearning.repository.VocabularyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdminCurriculumService {
  private static final String EMPTY_JSON_ARRAY = "[]";

  private final LessonRepository lessonRepository;
  private final LessonExerciseRepository lessonExerciseRepository;
  private final VocabularyRepository vocabularyRepository;
  private final UserProgressRepository userProgressRepository;
  private final ObjectMapper objectMapper;

  public AdminCurriculumService(
      LessonRepository lessonRepository,
      LessonExerciseRepository lessonExerciseRepository,
      VocabularyRepository vocabularyRepository,
      UserProgressRepository userProgressRepository,
      ObjectMapper objectMapper
  ) {
    this.lessonRepository = lessonRepository;
    this.lessonExerciseRepository = lessonExerciseRepository;
    this.vocabularyRepository = vocabularyRepository;
    this.userProgressRepository = userProgressRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(readOnly = true)
  public List<AdminLessonSummaryDto> listLessons(LessonLevel levelFilter) {
    List<LessonEntity> lessons =
        levelFilter == null ? lessonRepository.findAll() : lessonRepository.findByLevel(levelFilter);
    List<AdminLessonSummaryDto> out = new ArrayList<>();
    for (LessonEntity l : lessons) {
      out.add(new AdminLessonSummaryDto(
          l.getId(),
          l.getTitle(),
          l.getLevel().name(),
          l.getCategory().name(),
          l.getXpReward(),
          l.getMenschenOrder(),
          lessonExerciseRepository.countByLesson_Id(l.getId()),
          vocabularyRepository.countByLesson_Id(l.getId())
      ));
    }
    out.sort((a, b) -> {
      Integer ao = a.menschenOrder();
      Integer bo = b.menschenOrder();
      if (ao != null && bo != null && !ao.equals(bo)) return ao.compareTo(bo);
      if (ao != null && bo == null) return -1;
      if (ao == null && bo != null) return 1;
      return Long.compare(a.id(), b.id());
    });
    return out;
  }

  @Transactional(readOnly = true)
  public AdminLessonDetailDto getLessonDetail(long lessonId) {
    LessonEntity lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new NotFoundException("LESSON_NOT_FOUND", "Không tìm thấy bài id=" + lessonId));
    return buildDetail(lesson);
  }

  @Transactional
  public AdminLessonDetailDto createLesson(AdminCreateLessonRequest req) {
    LessonLevel level = parseLevel(req.level());
    LessonCategory category = parseCategory(req.category());
    assertMenschenOrderFree(level, req.menschenOrder(), null);

    LessonEntity lesson = LessonEntity.builder()
        .title(req.title().trim())
        .level(level)
        .category(category)
        .xpReward(req.xpReward())
        .menschenOrder(req.menschenOrder())
        .usageNotesJson(EMPTY_JSON_ARRAY)
        .examplePhrasesJson(EMPTY_JSON_ARRAY)
        .build();
    lesson = lessonRepository.save(lesson);
    return buildDetail(lesson);
  }

  @Transactional
  public AdminLessonDetailDto replaceLessonContent(long lessonId, AdminReplaceLessonContentRequest req) {
    LessonEntity lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new NotFoundException("LESSON_NOT_FOUND", "Không tìm thấy bài id=" + lessonId));

    LessonLevel newLevel = parseLevel(req.level());
    LessonCategory newCategory = parseCategory(req.category());
    assertMenschenOrderFree(newLevel, req.menschenOrder(), lessonId);

    lesson.setTitle(req.title().trim());
    lesson.setLevel(newLevel);
    lesson.setCategory(newCategory);
    lesson.setXpReward(req.xpReward());
    lesson.setMenschenOrder(req.menschenOrder());
    lesson.setUsageNotesJson(writeUsageNotesJson(req.usageNotes()));
    lesson.setExamplePhrasesJson(writeExamplePhrasesJson(req.examplePhrases()));
    lessonRepository.save(lesson);
    return buildDetail(lesson);
  }

  @Transactional
  public void deleteLesson(long lessonId) {
    LessonEntity lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new NotFoundException("LESSON_NOT_FOUND", "Không tìm thấy bài id=" + lessonId));
    if (userProgressRepository.existsByLesson_Id(lessonId)) {
      throw new ConflictException(
          "LESSON_HAS_PROGRESS",
          "Không xóa được: đã có học viên ghi nhận tiến độ bài này."
      );
    }
    lessonExerciseRepository.deleteAll(lessonExerciseRepository.findByLesson_IdOrderBySortOrderAsc(lessonId));
    vocabularyRepository.deleteAll(vocabularyRepository.findByLesson_Id(lessonId));
    lessonRepository.delete(lesson);
  }

  @Transactional
  public AdminLessonDetailDto createExercise(long lessonId, AdminCreateExerciseRequest req) {
    LessonEntity lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new NotFoundException("LESSON_NOT_FOUND", "Không tìm thấy bài id=" + lessonId));
    ExerciseType type = parseExerciseType(req.exerciseType());
    validateExercisePayload(type, req.choices(), req.correctAnswer());

    int sortOrder = req.sortOrder() != null
        ? req.sortOrder()
        : nextExerciseSortOrder(lessonId);

    LessonExerciseEntity entity = LessonExerciseEntity.builder()
        .lesson(lesson)
        .exerciseType(type)
        .questionText(req.questionText().trim())
        .choicesJson(writeChoicesJson(type, req.choices()))
        .correctAnswer(req.correctAnswer().trim())
        .sortOrder(sortOrder)
        .build();
    lessonExerciseRepository.save(entity);
    return buildDetail(lesson);
  }

  @Transactional
  public AdminLessonDetailDto replaceExercise(long exerciseId, AdminReplaceExerciseRequest req) {
    LessonExerciseEntity e = lessonExerciseRepository.findById(exerciseId)
        .orElseThrow(() -> new NotFoundException("EXERCISE_NOT_FOUND", "Không tìm thấy bài tập id=" + exerciseId));
    ExerciseType type = parseExerciseType(req.exerciseType());
    validateExercisePayload(type, req.choices(), req.correctAnswer());

    e.setExerciseType(type);
    e.setQuestionText(req.questionText().trim());
    e.setChoicesJson(writeChoicesJson(type, req.choices()));
    e.setCorrectAnswer(req.correctAnswer().trim());
    e.setSortOrder(req.sortOrder());
    lessonExerciseRepository.save(e);
    return buildDetail(e.getLesson());
  }

  @Transactional
  public AdminLessonDetailDto deleteExercise(long exerciseId) {
    LessonExerciseEntity e = lessonExerciseRepository.findById(exerciseId)
        .orElseThrow(() -> new NotFoundException("EXERCISE_NOT_FOUND", "Không tìm thấy bài tập id=" + exerciseId));
    LessonEntity lesson = e.getLesson();
    lessonExerciseRepository.delete(e);
    return buildDetail(lesson);
  }

  @Transactional
  public AdminLessonDetailDto createVocabulary(long lessonId, AdminCreateVocabularyRequest req) {
    LessonEntity lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new NotFoundException("LESSON_NOT_FOUND", "Không tìm thấy bài id=" + lessonId));
    VocabularyEntity v = VocabularyEntity.builder()
        .lesson(lesson)
        .word(req.word().trim())
        .article(trimToNull(req.article()))
        .pluralForm(trimToNull(req.pluralForm()))
        .meaningVi(req.meaningVi().trim())
        .pronunciationIpa(trimToNull(req.pronunciationIpa()))
        .usageNoteVi(trimToNull(req.usageNoteVi()))
        .audioUrl(trimToNull(req.audioUrl()))
        .build();
    vocabularyRepository.save(v);
    return buildDetail(lesson);
  }

  @Transactional
  public AdminLessonDetailDto replaceVocabulary(long vocabularyId, AdminReplaceVocabularyRequest req) {
    VocabularyEntity v = vocabularyRepository.findById(vocabularyId)
        .orElseThrow(() -> new NotFoundException("VOCABULARY_NOT_FOUND", "Không tìm thấy từ vựng id=" + vocabularyId));
    v.setWord(req.word().trim());
    v.setArticle(trimToNull(req.article()));
    v.setPluralForm(trimToNull(req.pluralForm()));
    v.setMeaningVi(req.meaningVi().trim());
    v.setPronunciationIpa(trimToNull(req.pronunciationIpa()));
    v.setUsageNoteVi(trimToNull(req.usageNoteVi()));
    v.setAudioUrl(trimToNull(req.audioUrl()));
    vocabularyRepository.save(v);
    return buildDetail(v.getLesson());
  }

  @Transactional
  public AdminLessonDetailDto deleteVocabulary(long vocabularyId) {
    VocabularyEntity v = vocabularyRepository.findById(vocabularyId)
        .orElseThrow(() -> new NotFoundException("VOCABULARY_NOT_FOUND", "Không tìm thấy từ vựng id=" + vocabularyId));
    LessonEntity lesson = v.getLesson();
    vocabularyRepository.delete(v);
    return buildDetail(lesson);
  }

  private int nextExerciseSortOrder(long lessonId) {
    List<LessonExerciseEntity> list = lessonExerciseRepository.findByLesson_IdOrderBySortOrderAsc(lessonId);
    return list.stream().mapToInt(LessonExerciseEntity::getSortOrder).max().orElse(0) + 1;
  }

  private void assertMenschenOrderFree(LessonLevel level, Integer order, Long excludeLessonId) {
    if (order == null) {
      return;
    }
    lessonRepository.findByLevelAndMenschenOrder(level, order)
        .filter(l -> excludeLessonId == null || !l.getId().equals(excludeLessonId))
        .ifPresent(l -> {
          throw new ConflictException(
              "MENSCHEN_ORDER_TAKEN",
              "Thứ tự Menschen " + order + " đã tồn tại ở level " + level.name()
          );
        });
  }

  private LessonLevel parseLevel(String raw) {
    try {
      return LessonLevel.valueOf(raw.trim().toUpperCase(Locale.ROOT));
    } catch (Exception e) {
      throw new BadRequestException("INVALID_LEVEL", "Level không hợp lệ: " + raw);
    }
  }

  private LessonCategory parseCategory(String raw) {
    try {
      return LessonCategory.valueOf(raw.trim().toUpperCase(Locale.ROOT));
    } catch (Exception e) {
      throw new BadRequestException("INVALID_CATEGORY", "Category không hợp lệ: " + raw);
    }
  }

  private ExerciseType parseExerciseType(String raw) {
    try {
      return ExerciseType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
    } catch (Exception e) {
      throw new BadRequestException("INVALID_EXERCISE_TYPE", "Loại bài tập không hợp lệ: " + raw);
    }
  }

  private void validateExercisePayload(ExerciseType type, List<String> choices, String correctAnswer) {
    if (type == ExerciseType.MCQ) {
      if (choices == null || choices.size() < 2) {
        throw new BadRequestException("MCQ_CHOICES", "MCQ cần ít nhất 2 lựa chọn.");
      }
      String ca = correctAnswer.trim();
      boolean match = choices.stream().map(String::trim).anyMatch(c -> c.equals(ca));
      if (!match) {
        throw new BadRequestException("MCQ_CORRECT_ANSWER", "Đáp án đúng phải trùng một trong các lựa chọn.");
      }
    } else {
      if (choices != null && !choices.isEmpty()) {
        throw new BadRequestException("SHORT_TEXT_CHOICES", "SHORT_TEXT không dùng danh sách lựa chọn.");
      }
    }
  }

  private String writeChoicesJson(ExerciseType type, List<String> choices) {
    if (type != ExerciseType.MCQ) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(choices.stream().map(String::trim).toList());
    } catch (Exception e) {
      throw new BadRequestException("CHOICES_JSON", "Không ghi được choices JSON");
    }
  }

  private String writeUsageNotesJson(List<String> notes) {
    try {
      return objectMapper.writeValueAsString(notes == null ? List.of() : notes);
    } catch (Exception e) {
      throw new BadRequestException("USAGE_JSON", "Không ghi được usage_notes_json");
    }
  }

  private String writeExamplePhrasesJson(List<ExamplePhraseAdminDto> phrases) {
    try {
      List<ExamplePhraseJsonRow> rows = phrases.stream()
          .map(p -> new ExamplePhraseJsonRow(
              p.german().trim(),
              p.vietnamese() == null ? "" : p.vietnamese().trim(),
              p.audioUrl() == null || p.audioUrl().isBlank() ? null : p.audioUrl().trim()
          ))
          .toList();
      return objectMapper.writeValueAsString(rows);
    } catch (Exception e) {
      throw new BadRequestException("EXAMPLES_JSON", "Không ghi được example_phrases_json");
    }
  }

  private AdminLessonDetailDto buildDetail(LessonEntity lesson) {
    long lid = lesson.getId();
    AdminLessonMetaDto meta = new AdminLessonMetaDto(
        lid,
        lesson.getTitle(),
        lesson.getLevel().name(),
        lesson.getCategory().name(),
        lesson.getXpReward(),
        lesson.getMenschenOrder()
    );

    List<String> usageNotes = parseUsageNotesJson(lesson.getUsageNotesJson());
    List<ExamplePhraseAdminDto> examples = parseExamplePhrasesJson(lesson.getExamplePhrasesJson());

    List<AdminExerciseDto> exercises = lessonExerciseRepository.findByLesson_IdOrderBySortOrderAsc(lid).stream()
        .map(this::mapExercise)
        .toList();

    List<AdminVocabularyDto> vocabs = vocabularyRepository.findByLesson_Id(lid).stream()
        .map(this::mapVocabulary)
        .toList();

    return new AdminLessonDetailDto(meta, usageNotes, examples, exercises, vocabs);
  }

  private List<String> parseUsageNotesJson(String json) {
    if (json == null || json.isBlank()) {
      return List.of();
    }
    try {
      List<String> list = objectMapper.readValue(json, new TypeReference<>() {
      });
      return list == null ? List.of() : List.copyOf(list);
    } catch (Exception e) {
      return List.of();
    }
  }

  private List<ExamplePhraseAdminDto> parseExamplePhrasesJson(String json) {
    if (json == null || json.isBlank()) {
      return List.of();
    }
    try {
      List<ExamplePhraseJsonRow> rows = objectMapper.readValue(json, new TypeReference<>() {
      });
      if (rows == null) {
        return List.of();
      }
      return rows.stream()
          .map(r -> new ExamplePhraseAdminDto(
              r.german(),
              r.vietnamese() == null ? "" : r.vietnamese(),
              r.audioUrl()
          ))
          .toList();
    } catch (Exception e) {
      return List.of();
    }
  }

  private AdminExerciseDto mapExercise(LessonExerciseEntity e) {
    List<String> choices = List.of();
    if (e.getChoicesJson() != null && !e.getChoicesJson().isBlank()) {
      try {
        List<String> parsed = objectMapper.readValue(e.getChoicesJson(), new TypeReference<>() {
        });
        choices = parsed == null ? List.of() : List.copyOf(parsed);
      } catch (Exception ignored) {
        choices = List.of();
      }
    }
    return new AdminExerciseDto(
        e.getId(),
        e.getLesson().getId(),
        e.getExerciseType().name(),
        e.getQuestionText(),
        choices,
        e.getCorrectAnswer(),
        e.getSortOrder()
    );
  }

  private AdminVocabularyDto mapVocabulary(VocabularyEntity v) {
    return new AdminVocabularyDto(
        v.getId(),
        v.getLesson().getId(),
        v.getWord(),
        v.getArticle(),
        v.getPluralForm(),
        v.getMeaningVi(),
        v.getPronunciationIpa(),
        v.getUsageNoteVi(),
        v.getAudioUrl()
    );
  }

  private static String trimToNull(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}
