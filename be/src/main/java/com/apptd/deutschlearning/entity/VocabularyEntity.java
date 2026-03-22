package com.apptd.deutschlearning.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vocabularies")
public class VocabularyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "lesson_id", nullable = false)
  private LessonEntity lesson;

  @Column(name = "word", nullable = false, length = 100)
  private String word;

  // article: der/die/das
  @Column(name = "article", length = 10)
  private String article;

  @Column(name = "plural_form", length = 100)
  private String pluralForm;

  @Column(name = "meaning_vi", nullable = false, length = 255)
  private String meaningVi;

  /** Phiên âm IPA (hiển thị tooltip). */
  @Column(name = "pronunciation_ipa", length = 160)
  private String pronunciationIpa;

  /** Gợi ý cách dùng / ví dụ ngắn (tiếng Việt). */
  @Column(name = "usage_note_vi", length = 600)
  private String usageNoteVi;

  @Column(name = "audio_url", length = 500)
  private String audioUrl;
}

