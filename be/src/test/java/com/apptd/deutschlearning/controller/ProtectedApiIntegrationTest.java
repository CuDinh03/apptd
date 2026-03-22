package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.entity.LessonEntity;
import com.apptd.deutschlearning.entity.LessonExerciseEntity;
import com.apptd.deutschlearning.entity.UserEntity;
import com.apptd.deutschlearning.entity.enums.ExerciseType;
import com.apptd.deutschlearning.entity.enums.LessonCategory;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.entity.enums.Role;
import com.apptd.deutschlearning.repository.LessonExerciseRepository;
import com.apptd.deutschlearning.repository.LessonRepository;
import com.apptd.deutschlearning.repository.UserRepository;
import com.apptd.deutschlearning.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API cần JWT + JSON lỗi 401/403; RBAC admin; tiến độ / bài tập / AI tắt.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProtectedApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LessonRepository lessonRepository;

  @Autowired
  private LessonExerciseRepository lessonExerciseRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void progressMe_withoutToken_returns401ApiResponse() throws Exception {
    mockMvc.perform(get("/api/v1/progress/me").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
  }

  @Test
  void progressMe_withValidJwt_returnsOk() throws Exception {
    UserEntity u = userRepository.save(UserEntity.builder()
        .username("int_user_progress")
        .passwordHash("x")
        .role(Role.USER)
        .totalXp(0)
        .build());
    String token = jwtService.createAccessToken(u.getId(), u.getRole().name());

    mockMvc.perform(
            get("/api/v1/progress/me")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.username").value("int_user_progress"));
  }

  @Test
  void adminHealth_userRole_returns403ApiResponse() throws Exception {
    UserEntity u = userRepository.save(UserEntity.builder()
        .username("int_user_not_admin")
        .passwordHash("x")
        .role(Role.USER)
        .totalXp(0)
        .build());
    String token = jwtService.createAccessToken(u.getId(), u.getRole().name());

    mockMvc.perform(
            get("/api/v1/admin/health")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
  }

  @Test
  void adminHealth_adminRole_returnsOk() throws Exception {
    UserEntity a = userRepository.save(UserEntity.builder()
        .username("int_admin_health")
        .passwordHash("x")
        .role(Role.ADMIN)
        .totalXp(0)
        .build());
    String token = jwtService.createAccessToken(a.getId(), a.getRole().name());

    mockMvc.perform(
            get("/api/v1/admin/health")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.role").value("ADMIN"));
  }

  @Test
  void gradeExercise_returnsWrappedResult() throws Exception {
    UserEntity u = userRepository.save(UserEntity.builder()
        .username("int_user_exercise")
        .passwordHash("x")
        .role(Role.USER)
        .totalXp(0)
        .build());
    String token = jwtService.createAccessToken(u.getId(), u.getRole().name());

    LessonEntity lesson = lessonRepository.save(LessonEntity.builder()
        .title("L ex")
        .level(LessonLevel.A1)
        .category(LessonCategory.VOCABULARY)
        .xpReward(5)
        .build());

    LessonExerciseEntity ex = lessonExerciseRepository.save(LessonExerciseEntity.builder()
        .lesson(lesson)
        .exerciseType(ExerciseType.SHORT_TEXT)
        .questionText("Wie heißt du?")
        .choicesJson(null)
        .correctAnswer("Ich heiße Anna")
        .sortOrder(0)
        .build());

    String body = objectMapper.writeValueAsString(
        java.util.Map.of("exerciseId", ex.getId(), "answer", "Ich heisse Anna"));

    mockMvc.perform(
            post("/api/v1/exercises/grade")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.correct").value(true));
  }

  @Test
  void analyzeSpeech_whenAiDisabled_returns503ApiResponse() throws Exception {
    UserEntity u = userRepository.save(UserEntity.builder()
        .username("int_user_ai")
        .passwordHash("x")
        .role(Role.USER)
        .totalXp(0)
        .build());
    String token = jwtService.createAccessToken(u.getId(), u.getRole().name());

    mockMvc.perform(
            multipart("/api/v1/ai/analyze-speech")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("AI_DISABLED"));
  }
}
