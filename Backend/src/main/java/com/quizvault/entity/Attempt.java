package com.quizvault.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    private String quizId;

    private String subject;

    private Double score;

    private Integer totalPoints;

    private Double percentage;

    private LocalDateTime completedAt;

    @Column(columnDefinition = "LONGTEXT")
    private String answersJson;

    private String reason;
}
