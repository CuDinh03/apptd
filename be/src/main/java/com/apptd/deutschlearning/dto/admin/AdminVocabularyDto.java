package com.apptd.deutschlearning.dto.admin;

public record AdminVocabularyDto(
    long id,
    long lessonId,
    String word,
    String article,
    String pluralForm,
    String meaningVi,
    String pronunciationIpa,
    String usageNoteVi,
    String audioUrl
) {
}
