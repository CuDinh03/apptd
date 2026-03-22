package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.admin.*;
import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.exception.BadRequestException;
import com.apptd.deutschlearning.service.AdminCurriculumService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Quản trị lộ trình: bài học, bài tập, từ vựng — chỉ ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/curriculum")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCurriculumController {
  private final AdminCurriculumService adminCurriculumService;

  public AdminCurriculumController(AdminCurriculumService adminCurriculumService) {
    this.adminCurriculumService = adminCurriculumService;
  }

  @GetMapping("/lessons")
  public ApiResponse<List<AdminLessonSummaryDto>> listLessons(@RequestParam(required = false) String level) {
    LessonLevel filter = null;
    if (level != null && !level.isBlank()) {
      try {
        filter = LessonLevel.valueOf(level.trim().toUpperCase());
      } catch (IllegalArgumentException ex) {
        throw new BadRequestException("INVALID_LEVEL", "Tham số level không hợp lệ.");
      }
    }
    return ApiResponse.ok(adminCurriculumService.listLessons(filter));
  }

  @GetMapping("/lessons/{lessonId}")
  public ApiResponse<AdminLessonDetailDto> getLesson(@PathVariable long lessonId) {
    return ApiResponse.ok(adminCurriculumService.getLessonDetail(lessonId));
  }

  @PostMapping("/lessons")
  public ApiResponse<AdminLessonDetailDto> createLesson(@Valid @RequestBody AdminCreateLessonRequest request) {
    return ApiResponse.ok(adminCurriculumService.createLesson(request));
  }

  @PutMapping("/lessons/{lessonId}")
  public ApiResponse<AdminLessonDetailDto> replaceLesson(
      @PathVariable long lessonId,
      @Valid @RequestBody AdminReplaceLessonContentRequest request
  ) {
    return ApiResponse.ok(adminCurriculumService.replaceLessonContent(lessonId, request));
  }

  @DeleteMapping("/lessons/{lessonId}")
  public ApiResponse<Void> deleteLesson(@PathVariable long lessonId) {
    adminCurriculumService.deleteLesson(lessonId);
    return ApiResponse.ok(null);
  }

  @PostMapping("/lessons/{lessonId}/exercises")
  public ApiResponse<AdminLessonDetailDto> createExercise(
      @PathVariable long lessonId,
      @Valid @RequestBody AdminCreateExerciseRequest request
  ) {
    return ApiResponse.ok(adminCurriculumService.createExercise(lessonId, request));
  }

  @PutMapping("/exercises/{exerciseId}")
  public ApiResponse<AdminLessonDetailDto> replaceExercise(
      @PathVariable long exerciseId,
      @Valid @RequestBody AdminReplaceExerciseRequest request
  ) {
    return ApiResponse.ok(adminCurriculumService.replaceExercise(exerciseId, request));
  }

  @DeleteMapping("/exercises/{exerciseId}")
  public ApiResponse<AdminLessonDetailDto> deleteExercise(@PathVariable long exerciseId) {
    return ApiResponse.ok(adminCurriculumService.deleteExercise(exerciseId));
  }

  @PostMapping("/lessons/{lessonId}/vocabularies")
  public ApiResponse<AdminLessonDetailDto> createVocabulary(
      @PathVariable long lessonId,
      @Valid @RequestBody AdminCreateVocabularyRequest request
  ) {
    return ApiResponse.ok(adminCurriculumService.createVocabulary(lessonId, request));
  }

  @PutMapping("/vocabularies/{vocabularyId}")
  public ApiResponse<AdminLessonDetailDto> replaceVocabulary(
      @PathVariable long vocabularyId,
      @Valid @RequestBody AdminReplaceVocabularyRequest request
  ) {
    return ApiResponse.ok(adminCurriculumService.replaceVocabulary(vocabularyId, request));
  }

  @DeleteMapping("/vocabularies/{vocabularyId}")
  public ApiResponse<AdminLessonDetailDto> deleteVocabulary(@PathVariable long vocabularyId) {
    return ApiResponse.ok(adminCurriculumService.deleteVocabulary(vocabularyId));
  }
}
