package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.apptd.deutschlearning.dto.lesson.LessonContentResponse;
import com.apptd.deutschlearning.dto.lesson.LessonDto;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.service.LessonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
public class LessonsController {
  private final LessonService lessonService;

  public LessonsController(LessonService lessonService) {
    this.lessonService = lessonService;
  }

  @GetMapping
  public ApiResponse<List<LessonDto>> getLessons(@RequestParam(name = "level", required = false) LessonLevel level) {
    return ApiResponse.ok(lessonService.getLessonsByLevel(level));
  }

  @GetMapping("/{id}/content")
  public ApiResponse<LessonContentResponse> getLessonContent(@PathVariable("id") Long id) {
    return ApiResponse.ok(lessonService.getLessonContent(id));
  }
}
