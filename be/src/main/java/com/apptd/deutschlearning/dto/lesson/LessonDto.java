package com.apptd.deutschlearning.dto.lesson;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonDto {
  private Long id;
  private String title;
  private String level;     // A1/A2/B1/B2
  private String category;  // Ngữ pháp/Từ vựng/Giao tiếp
  private Integer menschenOrder; // 1..24 (A1) theo giáo trình Menschen
  private int xpReward;
}

