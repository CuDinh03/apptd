package com.apptd.deutschlearning.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeechAnalysisResponse {
  private int score; // 0-100
  private String feedback;
  private String corrections;
}

