package com.quizvault.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.quizvault.dto.CertificateDto;
import com.quizvault.entity.Certificate;
import com.quizvault.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public String generateCertificate(String userId, String studentName, String subject, String scoreStr, Double percentage, String attemptId) {
        if (attemptId != null) {
            Optional<Certificate> existing = certificateRepository.findByAttemptId(attemptId);
            if (existing.isPresent()) {
                return existing.get().getCertId();
            }
        }

        String certId = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Certificate cert = Certificate.builder()
                .certId(certId)
                .userId(userId)
                .studentName(studentName != null ? studentName : "Student")
                .subject(subject)
                .score(scoreStr)
                .percentage(percentage)
                .issuedAt(LocalDateTime.now())
                .attemptId(attemptId)
                .build();

        certificateRepository.save(cert);
        return certId;
    }

    public CertificateDto verifyCertificate(String certId) {
        Certificate cert = certificateRepository.findById(certId)
                .orElseThrow(() -> new RuntimeException("Certificate not found or invalid ID"));

        return CertificateDto.builder()
                .valid(true)
                .certId(cert.getCertId())
                .studentName(cert.getStudentName())
                .subject(cert.getSubject())
                .score(cert.getScore())
                .percentage(cert.getPercentage())
                .issuedAt(cert.getIssuedAt())
                .build();
    }

    public byte[] generatePdf(String certId) {
        Certificate cert = certificateRepository.findById(certId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Background / Border
            PdfPTable outerBorder = new PdfPTable(1);
            outerBorder.setWidthPercentage(100);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, Font.NORMAL, new Color(30, 27, 75));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.NORMAL, new Color(99, 102, 241));
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.NORMAL, new Color(15, 23, 42));
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
            Font certIdFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL, Color.GRAY);

            Paragraph pHeader = new Paragraph("KNOWLEDGE HUB · ASSESSMENT PORTAL", subtitleFont);
            pHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeader);

            document.add(new Paragraph(" "));

            Paragraph pTitle = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            document.add(new Paragraph(" "));

            Paragraph pSub = new Paragraph("This is proudly presented to", bodyFont);
            pSub.setAlignment(Element.ALIGN_CENTER);
            document.add(pSub);

            document.add(new Paragraph(" "));

            Paragraph pName = new Paragraph(cert.getStudentName(), nameFont);
            pName.setAlignment(Element.ALIGN_CENTER);
            document.add(pName);

            document.add(new Paragraph(" "));

            Paragraph pText = new Paragraph("for successfully completing the assessment in " + cert.getSubject() +
                    " with a score of " + cert.getScore() + " (" + Math.round(cert.getPercentage()) + "%).", bodyFont);
            pText.setAlignment(Element.ALIGN_CENTER);
            document.add(pText);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            String dateStr = cert.getIssuedAt() != null ? cert.getIssuedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "";
            Paragraph pFooter = new Paragraph("Issued on: " + dateStr + " | Certificate ID: " + cert.getCertId(), certIdFont);
            pFooter.setAlignment(Element.ALIGN_CENTER);
            document.add(pFooter);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating certificate PDF: " + e.getMessage());
        }
    }
}
