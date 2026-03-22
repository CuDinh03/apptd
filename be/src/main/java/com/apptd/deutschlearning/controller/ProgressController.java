package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.apptd.deutschlearning.dto.progress.SubmitProgressRequest;
import com.apptd.deutschlearning.dto.progress.SubmitProgressResponse;
import com.apptd.deutschlearning.dto.progress.UserProgressSummaryDto;
import com.apptd.deutschlearning.security.UserPrincipal;
import com.apptd.deutschlearning.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {
  private final ProgressService progressService;

  public ProgressController(ProgressService progressService) {
    this.progressService = progressService;
  }

  @GetMapping("/me")
  public ApiResponse<UserProgressSummaryDto> myProgress(@AuthenticationPrincipal UserPrincipal principal) {
    return ApiResponse.ok(progressService.getMyProgress(principal.getUserId()));
  }

  @PostMapping("/submit")
  public ApiResponse<SubmitProgressResponse> submit(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody SubmitProgressRequest request
  ) {
    return ApiResponse.ok(progressService.submitProgress(principal.getUserId(), request));
  }
}
