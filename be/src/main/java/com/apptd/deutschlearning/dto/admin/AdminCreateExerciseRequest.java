package com.apptd.deutschlearning.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AdminCreateExerciseRequest(
    @NotBlank String exerciseType,
    @NotBlank String questionText,
    List<String> choices,
    @NotBlank String correctAnswer,
    Integer sortOrder
) {
}
