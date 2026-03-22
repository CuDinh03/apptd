package com.apptd.deutschlearning.dto.admin;

import java.util.List;

public record AdminLessonDetailDto(
    AdminLessonMetaDto lesson,
    List<String> usageNotes,
    List<ExamplePhraseAdminDto> examplePhrases,
    List<AdminExerciseDto> exercises,
    List<AdminVocabularyDto> vocabularies
) {
}
