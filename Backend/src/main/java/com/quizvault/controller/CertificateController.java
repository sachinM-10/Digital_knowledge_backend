package com.quizvault.controller;

import com.quizvault.dto.CertificateDto;
import com.quizvault.entity.User;
import com.quizvault.repository.UserRepository;
import com.quizvault.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@RequestBody Map<String, Object> body, Authentication authentication) {
        String subject = (String) body.get("subject");
        Number score = (Number) body.get("score");
        Number total = (Number) body.get("total");
        String attemptId = (String) body.get("attemptId");

        String scoreStr = (score != null ? score : 0) + "/" + (total != null ? total : 10);
        double pct = (score != null && total != null && total.doubleValue() > 0) ? (score.doubleValue() / total.doubleValue()) * 100.0 : 0.0;

        String studentName = "Student";
        String userId = "anonymous";
        if (authentication != null && authentication.getName() != null) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null) {
                studentName = user.getDisplayName();
                userId = user.getId();
            }
        }

        String certId = certificateService.generateCertificate(userId, studentName, subject, scoreStr, pct, attemptId);
        return ResponseEntity.ok(Collections.singletonMap("certId", certId));
    }

    @GetMapping("/verify/{certId}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certId) {
        try {
            CertificateDto dto = certificateService.verifyCertificate(certId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Certificate not found or invalid ID."));
        }
    }

    @GetMapping("/{certId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String certId) {
        byte[] pdfBytes = certificateService.generatePdf(certId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Certificate_" + certId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
