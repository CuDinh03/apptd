package com.apptd.deutschlearning.dto.progress;

import java.util.List;

public record UserProgressSummaryDto(
    String username,
    long totalXp,
    List<LessonProgressRowDto> lessons
) {
}
