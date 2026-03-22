package com.apptd.deutschlearning.repository;

import com.apptd.deutschlearning.entity.UserProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgressEntity, Long> {
  boolean existsByLesson_Id(Long lessonId);

  Optional<UserProgressEntity> findByUser_IdAndLesson_Id(Long userId, Long lessonId);

  @Query("SELECT up FROM UserProgressEntity up JOIN FETCH up.lesson WHERE up.user.id = :userId")
  List<UserProgressEntity> findAllByUserIdWithLesson(@Param("userId") Long userId);
}

