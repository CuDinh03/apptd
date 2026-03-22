package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.config.seed.MenschenA1CurriculumSeed;
import com.apptd.deutschlearning.config.seed.MenschenA1CurriculumSeed.LessonSpec;
import com.apptd.deutschlearning.dto.lesson.*;
import com.apptd.deutschlearning.entity.LessonEntity;
import com.apptd.deutschlearning.entity.LessonExerciseEntity;
import com.apptd.deutschlearning.entity.SpeakingTopicEntity;
import com.apptd.deutschlearning.entity.VocabularyEntity;
import com.apptd.deutschlearning.entity.enums.LessonCategory;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.exception.NotFoundException;
import com.apptd.deutschlearning.repository.LessonExerciseRepository;
import com.apptd.deutschlearning.repository.LessonRepository;
import com.apptd.deutschlearning.repository.SpeakingTopicRepository;
import com.apptd.deutschlearning.repository.VocabularyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {
  private final LessonRepository lessonRepository;
  private final VocabularyRepository vocabularyRepository;
  private final SpeakingTopicRepository speakingTopicRepository;
  private final LessonExerciseRepository lessonExerciseRepository;
  private final ObjectMapper objectMapper;

  public LessonService(
      LessonRepository lessonRepository,
      VocabularyRepository vocabularyRepository,
      SpeakingTopicRepository speakingTopicRepository,
      LessonExerciseRepository lessonExerciseRepository,
      ObjectMapper objectMapper
  ) {
    this.lessonRepository = lessonRepository;
    this.vocabularyRepository = vocabularyRepository;
    this.speakingTopicRepository = speakingTopicRepository;
    this.lessonExerciseRepository = lessonExerciseRepository;
    this.objectMapper = objectMapper;
  }

  public List<LessonDto> getLessonsByLevel(LessonLevel level) {
    List<LessonEntity> lessons = (level == null) ? lessonRepository.findAll() : lessonRepository.findByLevel(level);
    return lessons.stream().map(this::mapLesson).toList();
  }

  public LessonContentResponse getLessonContent(Long lessonId) {
    LessonEntity lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new NotFoundException("LESSON_NOT_FOUND", "Không tìm thấy bài học id=" + lessonId));

    List<VocabularyEntity> vocabularies = vocabularyRepository.findByLesson_Id(lesson.getId());
    List<SpeakingTopicEntity> speakingTopics = speakingTopicRepository.findByLevelRequirement(lesson.getLevel());
    List<LessonExerciseEntity> exerciseEntities = lessonExerciseRepository.findByLesson_IdOrderBySortOrderAsc(lesson.getId());

    List<String> usageNotes = parseUsageNotesJson(lesson.getUsageNotesJson());
    List<ExamplePhraseDto> examplePhrases = parseExamplePhrasesJson(lesson.getExamplePhrasesJson());
    if (usageNotes.isEmpty() && examplePhrases.isEmpty()) {
      usageNotes = fallbackUsageNotesFromSeed(lesson);
      examplePhrases = fallbackExamplesFromSeed(lesson);
    }

    return LessonContentResponse.builder()
        .lesson(mapLesson(lesson))
        .vocabularies(vocabularies.stream().map(this::mapVocabulary).toList())
        .usageNotes(usageNotes)
        .examplePhrases(examplePhrases)
        .speakingTopics(speakingTopics.stream().map(this::mapSpeakingTopic).toList())
        .exercises(exerciseEntities.stream().map(this::mapExercise).toList())
        .build();
  }

  /** Khi DB chưa có JSON (chưa migrate / chưa seed lại), lấy từ curriculum trong code. */
  private List<String> fallbackUsageNotesFromSeed(LessonEntity lesson) {
    return MenschenA1CurriculumSeed.findSpecForLesson(lesson.getLevel(), lesson.getMenschenOrder(), lesson.getTitle())
        .map(spec -> List.copyOf(spec.usageNotesVi()))
        .orElse(List.of());
  }

  private List<ExamplePhraseDto> fallbackExamplesFromSeed(LessonEntity lesson) {
    return MenschenA1CurriculumSeed.findSpecForLesson(lesson.getLevel(), lesson.getMenschenOrder(), lesson.getTitle())
        .map(this::mapExamplePhrasesFromSpec)
        .orElse(List.of());
  }

  private List<ExamplePhraseDto> mapExamplePhrasesFromSpec(LessonSpec spec) {
    return spec.examplePhrases().stream()
        .map(p -> ExamplePhraseDto.builder()
            .german(p.german())
            .vietnamese(p.vietnamese())
            .audioUrl(null)
            .build())
        .toList();
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

  private List<ExamplePhraseDto> parseExamplePhrasesJson(String json) {
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
          .map(r -> ExamplePhraseDto.builder()
              .german(r.german())
              .vietnamese(r.vietnamese())
              .audioUrl(r.audioUrl())
              .build())
          .toList();
    } catch (Exception e) {
      return List.of();
    }
  }

  private LessonDto mapLesson(LessonEntity lesson) {
    return LessonDto.builder()
        .id(lesson.getId())
        .title(lesson.getTitle())
        .level(lesson.getLevel().name())
        .category(mapCategoryVi(lesson.getCategory()))
        .menschenOrder(lesson.getMenschenOrder())
        .xpReward(lesson.getXpReward())
        .build();
  }

  private VocabularyDto mapVocabulary(VocabularyEntity vocabulary) {
    return VocabularyDto.builder()
        .word(vocabulary.getWord())
        .article(vocabulary.getArticle())
        .pluralForm(vocabulary.getPluralForm())
        .meaningVi(vocabulary.getMeaningVi())
        .pronunciationIpa(vocabulary.getPronunciationIpa())
        .usageNoteVi(vocabulary.getUsageNoteVi())
        .audioUrl(vocabulary.getAudioUrl())
        .build();
  }

  private SpeakingTopicDto mapSpeakingTopic(SpeakingTopicEntity topic) {
    return SpeakingTopicDto.builder()
        .id(topic.getId())
        .contextPrompt(topic.getContextPrompt())
        .levelRequirement(topic.getLevelRequirement().name())
        .build();
  }

  private ExerciseDto mapExercise(LessonExerciseEntity e) {
    List<String> choices = List.of();
    if (e.getChoicesJson() != null && !e.getChoicesJson().isBlank()) {
      try {
        choices = objectMapper.readValue(e.getChoicesJson(), new TypeReference<>() {
        });
      } catch (Exception ignored) {
        choices = List.of();
      }
    }
    return new ExerciseDto(e.getId(), e.getExerciseType().name(), e.getQuestionText(), choices);
  }

  private String mapCategoryVi(LessonCategory category) {
    return switch (category) {
      case GRAMMAR -> "Ngữ pháp";
      case VOCABULARY -> "Từ vựng";
      case COMMUNICATION -> "Giao tiếp";
    };
  }
}
