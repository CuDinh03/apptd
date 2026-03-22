package com.apptd.deutschlearning.entity;

import com.apptd.deutschlearning.entity.enums.LessonLevel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "speaking_topics")
public class SpeakingTopicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // TEXT trong Flyway; tránh @Lob (Hibernate 6 + Postgres map String → oid/CLOB, validate fail).
  @Column(name = "context_prompt", nullable = false, columnDefinition = "TEXT")
  private String contextPrompt;

  @Enumerated(EnumType.STRING)
  @Column(name = "level_requirement", nullable = false, length = 10)
  private LessonLevel levelRequirement;
}

