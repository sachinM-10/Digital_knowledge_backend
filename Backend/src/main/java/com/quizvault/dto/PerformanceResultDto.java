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
public class PerformanceResultDto {
    private String studentName;
    private String studentEmail;
    private String subject;
    private Integer numberOfAttempts;
    private Double highestScore;
    private Double latestScore;
    private Integer latestTotal;
    private Integer highestTotal;
    private LocalDateTime lastAttemptDate;
}
