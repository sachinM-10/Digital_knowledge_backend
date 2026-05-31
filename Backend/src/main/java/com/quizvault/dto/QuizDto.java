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
public class QuizDto {
    private String _id;
    private String title;
    private String description;
    private String category;
    private Integer timeLimitMinutes;
    private List<QuizQuestionDto> questions;
}
