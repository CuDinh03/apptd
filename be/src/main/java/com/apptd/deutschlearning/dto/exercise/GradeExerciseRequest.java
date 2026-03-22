package com.apptd.deutschlearning.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

/**
 * Gửi đáp án của học viên để backend chấm đúng/sai.
 * - MCQ: answer phải khớp với 1 lựa chọn.
 * - SHORT_TEXT: answer là câu trả lời ngắn.
 */
public record GradeExerciseRequest(
    @NotNull @Min(1) Long exerciseId,
    @NotBlank String answer
) {}

