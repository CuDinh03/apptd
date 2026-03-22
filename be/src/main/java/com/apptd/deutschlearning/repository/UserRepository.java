package com.apptd.deutschlearning.repository;

import com.apptd.deutschlearning.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);

  boolean existsByUsername(String username);

  List<UserEntity> findAllByOrderByTotalXpDesc(Pageable pageable);
}

