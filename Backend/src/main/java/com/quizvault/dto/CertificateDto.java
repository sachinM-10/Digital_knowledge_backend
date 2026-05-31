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
public class CertificateDto {
    private Boolean valid;
    private String certId;
    private String studentName;
    private String subject;
    private String score;
    private Double percentage;
    private LocalDateTime issuedAt;
}
