package com.quizvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDto {
    private String _id;
    private Object quizId; // Can be String or Map/DTO with title
    private String subject;
    private Double score;
    private Integer totalPoints;
    private Double percentage;
    private LocalDateTime completedAt;
}
