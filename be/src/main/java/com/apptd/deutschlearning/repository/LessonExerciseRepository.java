package com.apptd.deutschlearning.repository;

import com.apptd.deutschlearning.entity.LessonExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonExerciseRepository extends JpaRepository<LessonExerciseEntity, Long> {
  List<LessonExerciseEntity> findByLesson_IdOrderBySortOrderAsc(Long lessonId);

  long countByLesson_Id(Long lessonId);
}
