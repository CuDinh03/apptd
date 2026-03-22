package com.apptd.deutschlearning.dto.lesson;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LessonContentResponse {
  private LessonDto lesson;
  private List<VocabularyDto> vocabularies;
  /** Gợi ý cách dùng (tiếng Việt), hiển thị trước bài tập. */
  private List<String> usageNotes;
  /** Câu mẫu tiếng Đức + nghĩa tiếng Việt. */
  private List<ExamplePhraseDto> examplePhrases;
  private List<SpeakingTopicDto> speakingTopics;
  private List<ExerciseDto> exercises;
}

