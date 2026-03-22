package com.apptd.deutschlearning.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record AdminCreateVocabularyRequest(
    @NotBlank String word,
    String article,
    String pluralForm,
    @NotBlank String meaningVi,
    String pronunciationIpa,
    String usageNoteVi,
    String audioUrl
) {
}
