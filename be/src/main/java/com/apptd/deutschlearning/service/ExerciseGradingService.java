package com.apptd.deutschlearning.service;

import com.apptd.deutschlearning.entity.LessonExerciseEntity;
import com.apptd.deutschlearning.entity.enums.ExerciseType;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ExerciseGradingService {
  /**
   * Chuẩn hoá chuỗi để giảm sai khác nhỏ do khoảng trắng/hoa thường/kí tự đặc biệt.
   */
  private String normalize(String s) {
    if (s == null) return "";
    String out = s.trim();

    // Xử lý dấu ngoặc/quotes người dùng hay copy/paste.
    if ((out.startsWith("\"") && out.endsWith("\"")) || (out.startsWith("'") && out.endsWith("'"))) {
      out = out.substring(1, out.length() - 1).trim();
    }

    out = out.toLowerCase(Locale.GERMAN);

    // Cho phép gõ không dấu (phổ biến khi người dùng không có layout DE).
    out = out
        .replace("ä", "ae")
        .replace("ö", "oe")
        .replace("ü", "ue")
        .replace("ß", "ss");

    // Collapse whitespace.
    out = out.replaceAll("\\s+", " ");
    return out;
  }

  public boolean isCorrect(LessonExerciseEntity exercise, String answer) {
    if (exercise == null) return false;
    if (answer == null) return false;

    String normalizedAnswer = normalize(answer);
    String normalizedCorrect = normalize(exercise.getCorrectAnswer());

    ExerciseType type = exercise.getExerciseType();
    if (type == null) return false;

    if (type == ExerciseType.SHORT_TEXT) {
      return normalizedAnswer.equals(normalizedCorrect);
    }

    // MCQ: answer phải khớp với đúng lựa chọn.
    if (type == ExerciseType.MCQ) {
      return normalizedAnswer.equals(normalizedCorrect);
    }

    return false;
  }
}

