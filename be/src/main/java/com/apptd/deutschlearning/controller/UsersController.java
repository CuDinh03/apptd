package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.apptd.deutschlearning.dto.leaderboard.LeaderboardEntryDto;
import com.apptd.deutschlearning.service.UserLeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
  private final UserLeaderboardService userLeaderboardService;

  public UsersController(UserLeaderboardService userLeaderboardService) {
    this.userLeaderboardService = userLeaderboardService;
  }

  @GetMapping("/leaderboard")
  public ApiResponse<List<LeaderboardEntryDto>> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
    return ApiResponse.ok(userLeaderboardService.getLeaderboard(limit));
  }
}
