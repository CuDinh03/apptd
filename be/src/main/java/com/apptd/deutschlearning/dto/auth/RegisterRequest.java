package com.apptd.deutschlearning.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Đăng ký tài khoản (Java Record theo .cursorrules).
 */
public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 64) String username,
    @NotBlank @Size(min = 8, max = 128) String password
) {
}
