package com.quizvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
    private String _id;
    private Object userId; // Map with { name, username, email }
    private String subject;
    private Integer ratingDifficulty;
    private Integer ratingQuality;
    private String suggestionText;
    private Object reportedQuestionId; // Map with { question } or null
    private String reportText;
    private LocalDateTime createdAt;
}
