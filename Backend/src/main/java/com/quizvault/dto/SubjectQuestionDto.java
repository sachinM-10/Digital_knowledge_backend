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
public class SubjectQuestionDto {
    private String _id;
    private String subject;
    private Integer bank;
    private String question;
    private List<String> options;
    private String correctAnswer;
}
