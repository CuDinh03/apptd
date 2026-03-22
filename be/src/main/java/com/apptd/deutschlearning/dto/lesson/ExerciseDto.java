package com.apptd.deutschlearning.dto.lesson;

import java.util.List;

/**
 * Bài tập trả về cho học viên — không lộ đáp án đúng (chỉ câu hỏi + lựa chọn nếu MCQ).
 */
public record ExerciseDto(Long id, String type, String questionText, List<String> choices) {
}
