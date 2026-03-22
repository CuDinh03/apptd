package com.apptd.deutschlearning.controller;

import com.apptd.deutschlearning.dto.lesson.LessonDto;
import com.apptd.deutschlearning.service.LessonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LessonsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LessonService lessonService;

  @Test
  void getLessons_returnsWrappedApiResponse() throws Exception {
    when(lessonService.getLessonsByLevel(any())).thenReturn(List.of(
        LessonDto.builder().id(1L).title("Test").level("A1").category("Từ vựng").xpReward(10).build()
    ));

    mockMvc.perform(get("/api/v1/lessons").param("level", "A1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].title").value("Test"));
  }

  @Test
  void getLessons_allLevels_usesNullLevel() throws Exception {
    when(lessonService.getLessonsByLevel(null)).thenReturn(List.of());
    mockMvc.perform(get("/api/v1/lessons"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray());
  }
}
