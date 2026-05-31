package com.quizvault.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    private String certId;

    @Column(nullable = false)
    private String userId;

    private String studentName;

    @Column(nullable = false)
    private String subject;

    private String score; // e.g. "8/10"

    private Double percentage;

    private LocalDateTime issuedAt;

    private String attemptId;
}
