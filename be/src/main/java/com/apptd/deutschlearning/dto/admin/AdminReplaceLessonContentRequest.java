package com.apptd.deutschlearning.dto.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * PUT bài học: thay toàn bộ meta + ghi chú + câu mẫu (JSON). Bài tập / từ vựng chỉnh qua API riêng.
 */
public record AdminReplaceLessonContentRequest(
    @NotBlank String title,
    @NotBlank String level,
    @NotBlank String category,
    @NotNull @Min(0) Integer xpReward,
    Integer menschenOrder,
    @NotNull List<String> usageNotes,
    @NotNull @Valid List<ExamplePhraseAdminDto> examplePhrases
) {
}
