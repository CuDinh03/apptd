package com.apptd.deutschlearning.dto.progress;

public record SubmitProgressResponse(
    Long lessonId,
    int scorePercent,
    long xpAwarded,
    long bonusXp,
    long totalXp,
    int lastScorePercent,
    int bestScorePercent,
    int attempts
) {
}
