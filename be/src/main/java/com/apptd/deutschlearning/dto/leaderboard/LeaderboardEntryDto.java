package com.apptd.deutschlearning.dto.leaderboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDto {
  private Long userId;
  private String username;
  private String role;
  private long totalXp;
}

