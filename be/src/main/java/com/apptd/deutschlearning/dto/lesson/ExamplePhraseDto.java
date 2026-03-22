package com.apptd.deutschlearning.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamplePhraseDto {
  private String german;
  private String vietnamese;
  /** URL file âm thanh (tùy chọn); nếu null FE dùng TTS trình duyệt. */
  private String audioUrl;
}
