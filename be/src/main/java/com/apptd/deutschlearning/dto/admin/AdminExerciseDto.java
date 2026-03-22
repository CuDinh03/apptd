package com.apptd.deutschlearning.dto.admin;

import java.util.List;

public record AdminExerciseDto(
    long id,
    long lessonId,
    String exerciseType,
    String questionText,
    List<String> choices,
    String correctAnswer,
    int sortOrder
) {
}
