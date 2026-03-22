package com.apptd.deutschlearning.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class JwtService {
  private final SecretKey secretKey;
  private final long expirationMs;
  private final String jwtSecretRaw;
  private final boolean enforceStrongJwtSecret;

  public JwtService(
      @Value("${jwt.secret}") String jwtSecret,
      @Value("${jwt.expiration-ms}") long expirationMs,
      @Value("${app.security.enforce-strong-jwt-secret:false}") boolean enforceStrongJwtSecret
  ) {
    this.jwtSecretRaw = jwtSecret;
    this.expirationMs = expirationMs;
    this.enforceStrongJwtSecret = enforceStrongJwtSecret;
    byte[] keyBytes = jwtSecret == null ? new byte[0] : jwtSecret.getBytes(StandardCharsets.UTF_8);
    // JJWT yêu cầu key đủ dài cho HS256. Với dev, pad/truncate để tránh crash app.
    int minLen = 32; // 256 bits
    if (keyBytes.length < minLen) {
      keyBytes = Arrays.copyOf(keyBytes, minLen);
    } else if (keyBytes.length > minLen) {
      keyBytes = Arrays.copyOf(keyBytes, minLen);
    }
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  @PostConstruct
  void validateSecretPolicy() {
    // P2: prod bắt buộc secret thật, không dùng mặc định "change_me".
    if (!enforceStrongJwtSecret) {
      return;
    }
    if (jwtSecretRaw == null
        || jwtSecretRaw.isBlank()
        || "change_me".equals(jwtSecretRaw.trim())
        || jwtSecretRaw.length() < 32) {
      throw new IllegalStateException(
          "JWT_SECRET không hợp lệ: cần >= 32 ký tự và khác 'change_me' khi app.security.enforce-strong-jwt-secret=true"
      );
    }
  }

  public long getExpirationMs() {
    return expirationMs;
  }

  /**
   * Tạo JWT cho user sau đăng nhập (subject = userId, claim role = USER|ADMIN).
   */
  public String createAccessToken(long userId, String roleName) {
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("role", roleName)
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plusMillis(expirationMs)))
        .signWith(secretKey)
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = parseToken(token);
      Date exp = claims.getExpiration();
      return exp == null || exp.after(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}
