package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.auth.AuthTokenResponse;
import com.apptd.deutschlearning.dto.auth.LoginRequest;
import com.apptd.deutschlearning.dto.auth.RegisterRequest;
import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.apptd.deutschlearning.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ApiResponse<AuthTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ApiResponse.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.ok(authService.login(request));
  }
}
