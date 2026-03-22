package com.apptd.deutschlearning.dto.common;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Wrapper chuẩn cho mọi response API (theo .cursorrules).
 */
@Getter
@Builder
public class ApiResponse<T> {
  private final boolean success;
  private final String message;
  private final String errorCode;
  private final T data;
  private final Instant timestamp;

  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message("OK")
        .errorCode(null)
        .data(data)
        .timestamp(Instant.now())
        .build();
  }

  public static <T> ApiResponse<T> ok(String message, T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .errorCode(null)
        .data(data)
        .timestamp(Instant.now())
        .build();
  }

  public static <T> ApiResponse<T> fail(String errorCode, String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .errorCode(errorCode)
        .data(null)
        .timestamp(Instant.now())
        .build();
  }
}
