package com.quizvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDto {
    private String _id;
    private String quizId;
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private Integer points;
    private Integer sortOrder;
}
