package com.quizvault.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String subject; // "C", "Python", "Java"

    @Column(nullable = false)
    private Integer bank; // 1, 2, 3, 4

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionsJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;
}
