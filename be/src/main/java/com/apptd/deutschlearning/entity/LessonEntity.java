package com.apptd.deutschlearning.entity;

import com.apptd.deutschlearning.entity.enums.LessonCategory;
import com.apptd.deutschlearning.entity.enums.LessonLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lessons")
public class LessonEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(name = "level", nullable = false, length = 10)
  private LessonLevel level;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 30)
  private LessonCategory category;

  @Column(name = "xp_reward", nullable = false)
  private int xpReward;

  /**
   * Thứ tự theo giáo trình Menschen A1 (1..24).
   * Dùng để điều hướng "lesson tiếp theo" đúng curriculum.
   */
  @Column(name = "menschen_order", nullable = true)
  private Integer menschenOrder;

  /** JSON mảng chuỗi (tiếng Việt): gợi ý cách dùng từ/cấu trúc trong bài. */
  @Column(name = "usage_notes_json", columnDefinition = "TEXT")
  private String usageNotesJson;

  /** JSON mảng object { "german", "vietnamese" }: câu mẫu. */
  @Column(name = "example_phrases_json", columnDefinition = "TEXT")
  private String examplePhrasesJson;

  @OneToMany(mappedBy = "lesson")
  private Set<VocabularyEntity> vocabularies = new HashSet<>();
}

