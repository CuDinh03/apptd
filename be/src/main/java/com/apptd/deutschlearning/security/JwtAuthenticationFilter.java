package com.apptd.deutschlearning.security;

import com.apptd.deutschlearning.entity.UserEntity;
import com.apptd.deutschlearning.entity.enums.Role;
import com.apptd.deutschlearning.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring("Bearer ".length());
    if (!jwtService.isTokenValid(token)) {
      // Không set Authentication để Spring Security trả 401 theo cấu hình.
      filterChain.doFilter(request, response);
      return;
    }

    try {
      Claims claims = jwtService.parseToken(token);
      long userId = Long.parseLong(claims.getSubject());
      Optional<UserEntity> userOpt = userRepository.findById(userId);
      if (userOpt.isEmpty()) {
        filterChain.doFilter(request, response);
        return;
      }

      UserEntity user = userOpt.get();
      Role role = user.getRole();
      UserPrincipal principal = new UserPrincipal(user.getId(), user.getUsername(), role);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception ignored) {
      // Token không hợp lệ -> bỏ qua.
    }

    filterChain.doFilter(request, response);
  }
}

