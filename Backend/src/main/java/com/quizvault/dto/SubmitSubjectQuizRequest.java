package com.quizvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSubjectQuizRequest {
    private String subject;
    private Map<String, String> answers;
    private String attemptId;
    private String reason;
}
