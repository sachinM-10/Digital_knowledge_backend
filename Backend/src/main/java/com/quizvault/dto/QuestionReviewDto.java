package com.quizvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionReviewDto {
    private String questionId;
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}
