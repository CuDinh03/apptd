package com.apptd.deutschlearning.dto.admin;

public record AdminLessonMetaDto(
    long id,
    String title,
    String level,
    String category,
    int xpReward,
    Integer menschenOrder
) {
}
