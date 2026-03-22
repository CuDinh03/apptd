package com.apptd.deutschlearning.repository;

import com.apptd.deutschlearning.entity.SpeakingTopicEntity;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakingTopicRepository extends JpaRepository<SpeakingTopicEntity, Long> {
  List<SpeakingTopicEntity> findByLevelRequirement(LessonLevel levelRequirement);
}

