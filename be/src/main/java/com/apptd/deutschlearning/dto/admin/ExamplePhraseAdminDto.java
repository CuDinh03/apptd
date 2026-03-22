package com.apptd.deutschlearning.dto.admin;

import jakarta.validation.constraints.NotBlank;

/**
 * Câu mẫu trong form admin (đồng bộ JSON example_phrases_json).
 */
public record ExamplePhraseAdminDto(
    @NotBlank String german,
    String vietnamese,
    String audioUrl
) {
}
