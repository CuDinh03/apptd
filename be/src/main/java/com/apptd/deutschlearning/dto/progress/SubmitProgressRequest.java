package com.apptd.deutschlearning.dto.progress;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SubmitProgressRequest(
    @NotNull Long lessonId,
    @NotNull @Min(0) @Max(100) Integer scorePercent
) {
}
