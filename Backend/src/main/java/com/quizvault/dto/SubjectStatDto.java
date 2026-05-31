package com.quizvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectStatDto {
    private String subject;
    private Long totalQuestions;
    private Long banksCount;
    private Long attemptsCount;
}
