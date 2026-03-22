package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.dto.leaderboard.LeaderboardEntryDto;
import com.apptd.deutschlearning.entity.UserEntity;
import com.apptd.deutschlearning.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLeaderboardService {
  private final UserRepository userRepository;

  public UserLeaderboardService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<LeaderboardEntryDto> getLeaderboard(int limit) {
    int safeLimit = Math.max(1, Math.min(100, limit));
    List<UserEntity> users = userRepository.findAllByOrderByTotalXpDesc(PageRequest.of(0, safeLimit));
    return users.stream().map(this::map).toList();
  }

  private LeaderboardEntryDto map(UserEntity user) {
    return LeaderboardEntryDto.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .role(user.getRole().name())
        .totalXp(user.getTotalXp())
        .build();
  }
}

