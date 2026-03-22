package com.apptd.deutschlearning.exception;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex, HttpServletRequest request) {
    ApiResponse<Void> body = ApiResponse.fail(ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(ex.getStatus()).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
    String detail = ex.getBindingResult().getAllErrors().stream()
        .filter(err -> err instanceof FieldError)
        .map(err -> ((FieldError) err).getField() + ": " + err.getDefaultMessage())
        .findFirst()
        .orElse("Validation failed");

    ApiResponse<Void> body = ApiResponse.fail("VALIDATION_ERROR", detail);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
    ApiResponse<Void> body = ApiResponse.fail("INVALID_REQUEST_BODY", "Request body is not readable");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  /**
   * @PreAuthorize / method security ném tại tầng MVC — không đi qua AccessDeniedHandler của filter chain.
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
    ApiResponse<Void> body = ApiResponse.fail("FORBIDDEN", "Không có quyền truy cập tài nguyên này.");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
  }

  /** Lỗi không dự kiến — log đầy đủ, client chỉ nhận thông điệp chung. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
    log.error("Unhandled exception", ex);
    ApiResponse<Void> body = ApiResponse.fail("INTERNAL_ERROR", "Lỗi máy chủ. Vui lòng thử lại sau.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
