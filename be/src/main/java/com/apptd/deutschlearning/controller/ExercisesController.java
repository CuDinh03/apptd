package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.apptd.deutschlearning.dto.exercise.GradeExerciseRequest;
import com.apptd.deutschlearning.dto.exercise.GradeExerciseResponse;
import com.apptd.deutschlearning.entity.LessonExerciseEntity;
import com.apptd.deutschlearning.exception.BadRequestException;
import com.apptd.deutschlearning.exception.NotFoundException;
import com.apptd.deutschlearning.repository.LessonExerciseRepository;
import com.apptd.deutschlearning.service.ExerciseGradingService;
import com.apptd.deutschlearning.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exercises")
public class ExercisesController {

  private final LessonExerciseRepository lessonExerciseRepository;
  private final ExerciseGradingService exerciseGradingService;

  public ExercisesController(
      LessonExerciseRepository lessonExerciseRepository,
      ExerciseGradingService exerciseGradingService
  ) {
    this.lessonExerciseRepository = lessonExerciseRepository;
    this.exerciseGradingService = exerciseGradingService;
  }

  @PostMapping("/grade")
  public ApiResponse<GradeExerciseResponse> grade(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody GradeExerciseRequest request
  ) {
    // principal chỉ dùng để đảm bảo endpoint yêu cầu xác thực (SecurityConfig: anyRequest.authenticated()).
    // Không cần userId cho việc chấm đúng/sai.
    if (request.answer() == null || request.answer().trim().isEmpty()) {
      throw new BadRequestException("ANSWER_REQUIRED", "Đáp án không được để trống");
    }

    LessonExerciseEntity exercise = lessonExerciseRepository.findById(request.exerciseId())
        .orElseThrow(() -> new NotFoundException("EXERCISE_NOT_FOUND", "Không tìm thấy exercise id=" + request.exerciseId()));

    boolean correct = exerciseGradingService.isCorrect(exercise, request.answer());
    return ApiResponse.ok(new GradeExerciseResponse(exercise.getId(), correct));
  }
}

