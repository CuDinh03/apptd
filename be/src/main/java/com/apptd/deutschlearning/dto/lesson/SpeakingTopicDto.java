package com.apptd.deutschlearning.dto.lesson;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeakingTopicDto {
  private Long id;
  private String contextPrompt;
  private String levelRequirement;
}

