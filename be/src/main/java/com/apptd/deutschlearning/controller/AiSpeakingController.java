package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.ai.SpeechAnalysisResponse;
import com.apptd.deutschlearning.dto.common.ApiResponse;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.exception.ServiceUnavailableException;
import com.apptd.deutschlearning.service.SpeechAnalysisService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/ai")
public class AiSpeakingController {
  private final ObjectProvider<SpeechAnalysisService> speechAnalysisServiceProvider;

  public AiSpeakingController(ObjectProvider<SpeechAnalysisService> speechAnalysisServiceProvider) {
    this.speechAnalysisServiceProvider = speechAnalysisServiceProvider;
  }

  @PostMapping(value = "/analyze-speech", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<SpeechAnalysisResponse> analyzeSpeech(
      @RequestPart(value = "audio", required = false) MultipartFile audio,
      @RequestParam(value = "text", required = false) String text,
      @RequestParam(value = "speakingTopicId", required = false) Long speakingTopicId,
      @RequestParam(value = "level", required = false) LessonLevel level
  ) throws IOException {
    SpeechAnalysisService speechAnalysisService = speechAnalysisServiceProvider.getIfAvailable();
    if (speechAnalysisService == null) {
      throw new ServiceUnavailableException("AI_DISABLED", "Tạm thời tắt chức năng AI.");
    }

    byte[] audioBytes = (audio != null && !audio.isEmpty()) ? audio.getBytes() : null;
    String audioFilename = (audio != null) ? audio.getOriginalFilename() : null;

    return ApiResponse.ok(speechAnalysisService.analyzeSpeech(audioBytes, audioFilename, text, speakingTopicId, level));
  }
}
