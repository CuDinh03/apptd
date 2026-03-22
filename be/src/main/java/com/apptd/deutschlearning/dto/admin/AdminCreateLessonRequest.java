package com.apptd.deutschlearning.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreateLessonRequest(
    @NotBlank String title,
    @NotBlank String level,
    @NotBlank String category,
    @NotNull @Min(0) Integer xpReward,
    Integer menschenOrder
) {
}
