package com.quizvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private Long totalStudents;
    private Long totalQuestions;
    private Long totalAttempts;
    private Long totalQuizzes;
}
