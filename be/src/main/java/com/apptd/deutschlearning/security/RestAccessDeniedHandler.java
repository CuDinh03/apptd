package com.apptd.deutschlearning.security;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Trả 403 JSON (ApiResponse) khi đã xác thực nhưng không đủ quyền (ví dụ USER gọi endpoint ADMIN).
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper objectMapper;

  public RestAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException
  ) throws java.io.IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiResponse<Void> body = ApiResponse.fail("FORBIDDEN", "Không có quyền truy cập tài nguyên này.");
    objectMapper.writeValue(response.getOutputStream(), body);
  }
}
