package com.apptd.deutschlearning.dto.auth;

public record AuthTokenResponse(
    String accessToken,
    String tokenType,
    long expiresInMs,
    long userId,
    String username,
    String role
) {
}
