package com.apptd.deutschlearning.dto.lesson;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Hàng JSON trong cột example_phrases_json (đồng bộ với seed).
 */
public record ExamplePhraseJsonRow(
    @JsonProperty("german") String german,
    @JsonProperty("vietnamese") String vietnamese,
    @JsonProperty("audioUrl") String audioUrl
) {
}
