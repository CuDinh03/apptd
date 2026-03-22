package com.apptd.deutschlearning.dto.admin;

public record AdminLessonSummaryDto(
    long id,
    String title,
    String level,
    String category,
    int xpReward,
    Integer menschenOrder,
    long exerciseCount,
    long vocabularyCount
) {
}
