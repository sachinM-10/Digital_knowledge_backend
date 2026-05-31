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
public class SubmitSubjectQuizResponse {
    private Integer score;
    private Integer total;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer unattemptedCount;
    private List<QuestionReviewDto> results;
    private String attemptId;
    private String subject;
    private String certId;
}
