package com.apptd.deutschlearning.dto.progress;

import java.time.Instant;

/**
 * Một dòng tiến độ theo bài học (để hiển thị tích luỹ XP theo bài đã làm).
 */
public record LessonProgressRowDto(
    Long lessonId,
    Integer menschenOrder,
    String lessonTitle,
    String level,
    String status,
    int bestScorePercent,
    int lastScorePercent,
    int attempts,
    long xpEarnedFromLesson,
    Instant completedAt
) {
}
