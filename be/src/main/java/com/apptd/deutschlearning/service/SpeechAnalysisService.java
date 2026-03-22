package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.dto.ai.SpeechAnalysisResponse;
import com.apptd.deutschlearning.entity.SpeakingTopicEntity;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import com.apptd.deutschlearning.exception.BadRequestException;
import com.apptd.deutschlearning.exception.NotFoundException;
import com.apptd.deutschlearning.repository.SpeakingTopicRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true")
public class SpeechAnalysisService {
  private final String provider;

  private final WebClient openAiClient;
  private final WebClient claudeClient;

  private final ObjectMapper objectMapper;
  private final SpeakingTopicRepository speakingTopicRepository;

  private final String openAiChatModel;
  private final String openAiTranscriptionModel;
  private final String claudeModel;

  public SpeechAnalysisService(
      @Value("${ai.provider}") String provider,
      @Value("${ai.openai.api-key}") String openAiApiKey,
      @Value("${ai.openai.base-url}") String openAiBaseUrl,
      @Value("${ai.openai.chat-model}") String openAiChatModel,
      @Value("${ai.openai.transcription-model}") String openAiTranscriptionModel,
      @Value("${ai.claude.api-key}") String claudeApiKey,
      @Value("${ai.claude.base-url}") String claudeBaseUrl,
      @Value("${ai.claude.model}") String claudeModel,
      SpeakingTopicRepository speakingTopicRepository,
      ObjectMapper objectMapper
  ) {
    this.provider = provider == null ? "openai" : provider.trim().toLowerCase();
    this.objectMapper = objectMapper;
    this.speakingTopicRepository = speakingTopicRepository;

    this.openAiChatModel = openAiChatModel;
    this.openAiTranscriptionModel = openAiTranscriptionModel;
    this.claudeModel = claudeModel;

    if (StringUtils.hasText(openAiApiKey)) {
      this.openAiClient = WebClient.builder()
          .baseUrl(openAiBaseUrl)
          .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
          .build();
    } else {
      this.openAiClient = null;
    }

    if (StringUtils.hasText(claudeApiKey)) {
      this.claudeClient = WebClient.builder()
          .baseUrl(claudeBaseUrl)
          .defaultHeader("x-api-key", claudeApiKey)
          .defaultHeader("anthropic-version", "2023-06-01")
          .build();
    } else {
      this.claudeClient = null;
    }
  }

  public SpeechAnalysisResponse analyzeSpeech(
      byte[] audioBytes,
      String audioFilename,
      String text,
      Long speakingTopicId,
      LessonLevel level
  ) {
    String transcriptOrText = resolveTranscriptOrText(audioBytes, audioFilename, text);
    String contextPrompt = resolveContextPrompt(speakingTopicId, level);

    String systemPrompt = """
        Bạn là AI chấm điểm phát âm tiếng Đức.
        Hãy chấm điểm phát âm (0-100), sửa lỗi ngữ pháp tiếng Đức trong câu người dùng, và gợi ý cách trả lời tự nhiên hơn.
        Luôn trả về JSON hợp lệ đúng schema:
        { "score": int, "feedback": string, "corrections": string }
        Không thêm bất kỳ trường nào khác ngoài 3 trường trên.
        """;

    String userPrompt = """
        Context chủ đề luyện nói (nếu có, hãy dùng để chấm nội dung và sự tự nhiên):
        %s

        Nội dung người dùng nói (transcript hoặc text):
        %s
        """.formatted(contextPrompt == null ? "" : contextPrompt, transcriptOrText);

    return switch (provider) {
      case "openai" -> analyzeWithOpenAi(systemPrompt, userPrompt);
      case "claude" -> analyzeWithClaude(systemPrompt, userPrompt);
      default -> throw new BadRequestException("AI_PROVIDER_UNSUPPORTED", "ai.provider không được hỗ trợ: " + provider);
    };
  }

  private SpeechAnalysisResponse analyzeWithOpenAi(String systemPrompt, String userPrompt) {
    if (openAiClient == null) {
      throw new BadRequestException("OPENAI_API_KEY_MISSING", "Thiếu OPENAI_API_KEY/AI_API_KEY để chạy provider=openai.");
    }

    OpenAiChatRequest chatRequest = new OpenAiChatRequest(
        openAiChatModel,
        List.of(
            new OpenAiChatRequest.Message("system", systemPrompt),
            new OpenAiChatRequest.Message("user", userPrompt)
        ),
        new OpenAiChatRequest.ResponseFormat("json_object")
    );

    JsonNode responseNode;
    try {
      responseNode = openAiClient.post()
          .uri("/v1/chat/completions")
          .contentType(APPLICATION_JSON)
          .bodyValue(chatRequest)
          .retrieve()
          .bodyToMono(JsonNode.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new BadRequestException(
          "AI_REQUEST_FAILED",
          "OpenAI request thất bại: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()
      );
    }

    String contentJson = extractFirstMessageContent(responseNode);
    try {
      return objectMapper.readValue(contentJson, SpeechAnalysisResponse.class);
    } catch (Exception e) {
      throw new BadRequestException("AI_INVALID_JSON", "OpenAI trả về JSON không đúng định dạng.");
    }
  }

  private SpeechAnalysisResponse analyzeWithClaude(String systemPrompt, String userPrompt) {
    if (claudeClient == null) {
      throw new BadRequestException("CLAUDE_API_KEY_MISSING", "Thiếu CLAUDE_API_KEY/AI_API_KEY để chạy provider=claude.");
    }

    // Claude có thể trả về JSON text; ta parse trực tiếp từ content.
    ClaudeMessagesRequest request = new ClaudeMessagesRequest(
        claudeModel,
        List.of(
            new ClaudeMessagesRequest.Message(
                "user",
                List.of(new ClaudeMessagesRequest.ContentBlock("text", userPrompt))
            )
        ),
        systemPrompt,
        800
    );

    JsonNode responseNode;
    try {
      responseNode = claudeClient.post()
          .uri("/v1/messages")
          .contentType(APPLICATION_JSON)
          .bodyValue(request)
          .retrieve()
          .bodyToMono(JsonNode.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new BadRequestException(
          "AI_REQUEST_FAILED",
          "Claude request thất bại: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()
      );
    }

    String contentJson = extractClaudeFirstText(responseNode);
    try {
      return objectMapper.readValue(contentJson, SpeechAnalysisResponse.class);
    } catch (Exception e) {
      throw new BadRequestException("AI_INVALID_JSON", "Claude trả về JSON không đúng định dạng.");
    }
  }

  private String resolveTranscriptOrText(byte[] audioBytes, String audioFilename, String text) {
    // Nếu có audio: dùng Whisper transcribe (dù provider là OpenAI hay Claude).
    if (audioBytes != null && audioBytes.length > 0) {
      if (openAiClient == null) {
        throw new BadRequestException(
            "OPENAI_WHISPER_REQUIRED",
            "Gửi audio yêu cầu phải có OPENAI_API_KEY để transcribe (Whisper)."
        );
      }

      String filename = StringUtils.hasText(audioFilename) ? audioFilename : "audio";
      MultipartBodyBuilder multipart = new MultipartBodyBuilder();
      multipart.part("model", openAiTranscriptionModel);
      multipart.part(
          "file",
          new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
              return filename;
            }
          }
      );
      WhisperResponse whisper = openAiClient.post()
          .uri("/v1/audio/transcriptions")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(multipart.build()))
          .retrieve()
          .bodyToMono(WhisperResponse.class)
          .block();

      if (!StringUtils.hasText(whisper.text)) {
        throw new BadRequestException("AI_TRANSCRIPTION_EMPTY", "Không trích xuất được transcript từ audio.");
      }
      return whisper.text;
    }

    if (!StringUtils.hasText(text)) {
      throw new BadRequestException("INPUT_REQUIRED", "Cần gửi audio hoặc text để phân tích.");
    }
    return text.trim();
  }

  private String resolveContextPrompt(Long speakingTopicId, LessonLevel level) {
    if (speakingTopicId != null) {
      SpeakingTopicEntity topic = speakingTopicRepository.findById(speakingTopicId)
          .orElseThrow(() -> new NotFoundException("SPEAKING_TOPIC_NOT_FOUND", "Không tìm thấy speakingTopicId=" + speakingTopicId));
      return topic.getContextPrompt();
    }

    if (level != null) {
      return speakingTopicRepository.findByLevelRequirement(level).stream()
          .findFirst()
          .map(SpeakingTopicEntity::getContextPrompt)
          .orElse("");
    }

    return "";
  }

  private String extractFirstMessageContent(JsonNode responseNode) {
    JsonNode choices = responseNode.get("choices");
    if (choices == null || !choices.isArray() || choices.size() == 0) {
      throw new BadRequestException("AI_NO_CHOICES", "AI không trả về choices.");
    }
    JsonNode message = choices.get(0).get("message");
    if (message == null) {
      throw new BadRequestException("AI_NO_MESSAGE", "AI không trả về message.");
    }
    JsonNode content = message.get("content");
    if (content == null || !content.isTextual()) {
      throw new BadRequestException("AI_NO_CONTENT", "AI không trả về content.");
    }
    return content.asText();
  }

  private String extractClaudeFirstText(JsonNode responseNode) {
    // Claude: { "content": [ { "type": "text", "text": "..." } ] }
    JsonNode content = responseNode.get("content");
    if (content == null || !content.isArray() || content.size() == 0) {
      throw new BadRequestException("AI_NO_CONTENT", "Claude không trả về content.");
    }
    for (JsonNode node : content) {
      if (node.has("text") && node.get("text").isTextual()) {
        return node.get("text").asText();
      }
    }
    throw new BadRequestException("AI_NO_CONTENT_TEXT", "Claude không trả về text.");
  }

  private record WhisperResponse(String text) {
  }

  private record OpenAiChatRequest(
      String model,
      List<Message> messages,
      @JsonProperty("response_format") ResponseFormat responseFormat
  ) {
    private record Message(String role, String content) {
    }

    private record ResponseFormat(String type) {
    }
  }

  private record ClaudeMessagesRequest(
      String model,
      List<Message> messages,
      String system,
      @JsonProperty("max_tokens") int maxTokens
  ) {
    private record Message(String role, List<ContentBlock> content) {
    }

    private record ContentBlock(String type, String text) {
    }
  }
}

