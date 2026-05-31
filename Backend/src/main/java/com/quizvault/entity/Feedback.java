package com.quizvault.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    private String attemptId;

    private String subject;

    private Integer ratingDifficulty;

    private Integer ratingQuality;

    @Column(columnDefinition = "TEXT")
    private String suggestionText;

    private String reportedQuestionId;

    @Column(columnDefinition = "TEXT")
    private String reportText;

    private LocalDateTime createdAt;
}
