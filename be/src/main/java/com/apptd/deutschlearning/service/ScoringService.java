package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.entity.LessonEntity;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

  /**
   * Tổng XP tối đa user được hưởng từ một bài học theo điểm % tốt nhất đã đạt,
   * không vượt quá {@link LessonEntity#getXpReward()} (tránh farm XP khi làm lại).
   */
  public long deservedTotalXpForLesson(LessonEntity lesson, int bestScorePercent) {
    int clamped = Math.min(100, Math.max(0, bestScorePercent));
    long cap = lesson.getXpReward();
    return Math.min(cap, Math.round(cap * (clamped / 100.0)));
  }
}
