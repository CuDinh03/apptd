package com.apptd.deutschlearning.entity;

import com.apptd.deutschlearning.entity.enums.ExerciseType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lesson_exercises")
public class LessonExerciseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "lesson_id", nullable = false)
  private LessonEntity lesson;

  @Enumerated(EnumType.STRING)
  @Column(name = "exercise_type", nullable = false, length = 30)
  private ExerciseType exerciseType;

  @Column(name = "question_text", nullable = false, length = 1000)
  private String questionText;

  /** JSON mảng các lựa chọn cho MCQ, ví dụ: ["Guten Tag","Guten Morgen"] */
  @Column(name = "choices_json", columnDefinition = "TEXT")
  private String choicesJson;

  /** Đáp án đúng (chuỗi khớp với một lựa chọn MCQ hoặc câu trả lời ngắn). */
  @Column(name = "correct_answer", nullable = false, length = 500)
  private String correctAnswer;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;
}
