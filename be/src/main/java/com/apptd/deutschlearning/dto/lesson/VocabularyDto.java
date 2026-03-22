package com.apptd.deutschlearning.dto.lesson;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VocabularyDto {
  private String word;
  private String article;     // der/die/das
  private String pluralForm;
  private String meaningVi;
  /** Phiên âm IPA (tooltip). */
  private String pronunciationIpa;
  /** Cách dùng / ví dụ (tooltip). */
  private String usageNoteVi;
  private String audioUrl;
}

