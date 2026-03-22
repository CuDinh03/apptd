package com.apptd.deutschlearning.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AdminReplaceExerciseRequest(
    @NotBlank String exerciseType,
    @NotBlank String questionText,
    List<String> choices,
    @NotBlank String correctAnswer,
    @NotNull Integer sortOrder
) {
}
