package com.apptd.deutschlearning.security;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Trả 401 JSON (ApiResponse) khi request chưa xác thực — thống nhất với GlobalExceptionHandler.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws java.io.IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiResponse<Void> body = ApiResponse.fail("UNAUTHORIZED", "Cần đăng nhập (JWT hợp lệ).");
    objectMapper.writeValue(response.getOutputStream(), body);
  }
}
