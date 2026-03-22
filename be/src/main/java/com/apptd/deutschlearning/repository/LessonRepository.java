package com.apptd.deutschlearning.repository;

import com.apptd.deutschlearning.entity.LessonEntity;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
  List<LessonEntity> findByLevel(LessonLevel level);

  boolean existsByLevelAndMenschenOrder(LessonLevel level, Integer menschenOrder);

  Optional<LessonEntity> findByLevelAndMenschenOrder(LessonLevel level, Integer menschenOrder);
}

