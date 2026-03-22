package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint nhạy cảm — chỉ ADMIN (minh hoạ RBAC theo .cursorrules).
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

  @GetMapping("/health")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Map<String, String>> health() {
    return ApiResponse.ok(Map.of("status", "UP", "role", "ADMIN"));
  }
}
