package com.apptd.deutschlearning.repository;

import com.apptd.deutschlearning.entity.VocabularyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabularyRepository extends JpaRepository<VocabularyEntity, Long> {
  List<VocabularyEntity> findByLesson_Id(Long lessonId);

  long countByLesson_Id(Long lessonId);
}

