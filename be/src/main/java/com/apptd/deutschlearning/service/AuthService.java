package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.dto.auth.AuthTokenResponse;
import com.apptd.deutschlearning.dto.auth.LoginRequest;
import com.apptd.deutschlearning.dto.auth.RegisterRequest;
import com.apptd.deutschlearning.entity.UserEntity;
import com.apptd.deutschlearning.entity.enums.Role;
import com.apptd.deutschlearning.exception.ConflictException;
import com.apptd.deutschlearning.exception.UnauthorizedException;
import com.apptd.deutschlearning.repository.UserRepository;
import com.apptd.deutschlearning.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthTokenResponse register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new ConflictException("USERNAME_TAKEN", "Username đã tồn tại");
    }
    UserEntity user = UserEntity.builder()
        .username(request.username().trim())
        .passwordHash(passwordEncoder.encode(request.password()))
        .role(Role.USER)
        .totalXp(0)
        .build();
    user = userRepository.save(user);
    return buildTokenResponse(user);
  }

  public AuthTokenResponse login(LoginRequest request) {
    UserEntity user = userRepository.findByUsername(request.username().trim())
        .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS", "Sai username hoặc password"));
    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new UnauthorizedException("INVALID_CREDENTIALS", "Sai username hoặc password");
    }
    return buildTokenResponse(user);
  }

  private AuthTokenResponse buildTokenResponse(UserEntity user) {
    String token = jwtService.createAccessToken(user.getId(), user.getRole().name());
    return new AuthTokenResponse(
        token,
        "Bearer",
        jwtService.getExpirationMs(),
        user.getId(),
        user.getUsername(),
        user.getRole().name()
    );
  }
}
