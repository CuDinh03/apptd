package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.entity.LessonEntity;
import com.apptd.deutschlearning.entity.enums.LessonCategory;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringServiceTest {

  private final ScoringService scoringService = new ScoringService();

  private static LessonEntity lesson(int xpReward) {
    return LessonEntity.builder()
        .id(1L)
        .title("L1")
        .level(LessonLevel.A1)
        .category(LessonCategory.GRAMMAR)
        .xpReward(xpReward)
        .build();
  }

  @Test
  void halfThenFull_capsAtLessonReward() {
    LessonEntity l = lesson(50);
    assertEquals(25, scoringService.deservedTotalXpForLesson(l, 50));
    assertEquals(50, scoringService.deservedTotalXpForLesson(l, 100));
  }

  @Test
  void neverExceedsXpReward() {
    assertEquals(50, scoringService.deservedTotalXpForLesson(lesson(50), 100));
    assertEquals(50, scoringService.deservedTotalXpForLesson(lesson(50), 200));
  }
}
