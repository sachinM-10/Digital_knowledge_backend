package com.quizvault.service;

import com.quizvault.entity.Attempt;
import com.quizvault.entity.User;
import com.quizvault.repository.AttemptRepository;
import com.quizvault.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private UserRepository userRepository;

    public byte[] generateExcelReport(String subject, String startDateStr, String endDateStr, String passFail) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            startDate = LocalDate.parse(startDateStr).atStartOfDay();
        }
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);
        }

        String filterSubject = (subject != null && !subject.equalsIgnoreCase("all")) ? subject : null;

        List<Attempt> attempts = attemptRepository.filterAttempts(filterSubject, startDate, endDate);

        // Additional in-memory filtering for completed attempts & pass/fail
        attempts = attempts.stream()
                .filter(a -> a.getCompletedAt() != null)
                .filter(a -> {
                    if (passFail == null || passFail.equalsIgnoreCase("all")) return true;
                    double pct = a.getPercentage() != null ? a.getPercentage() : 0.0;
                    if (passFail.equalsIgnoreCase("passed")) return pct >= 40.0;
                    if (passFail.equalsIgnoreCase("failed")) return pct < 40.0;
                    return true;
                })
                .collect(Collectors.toList());

        Map<String, User> userMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u, (u1, u2) -> u1));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Quiz Performance Results");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Student Name", "Student Email", "Subject", "Score", "Total Points", "Percentage (%)", "Status", "Attempt Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Attempt attempt : attempts) {
                User user = userMap.get(attempt.getUserId());
                Row row = sheet.createRow(rowIdx++);

                String studentName = user != null ? user.getDisplayName() : "Unknown";
                String studentEmail = user != null ? user.getEmail() : "N/A";
                double pct = attempt.getPercentage() != null ? attempt.getPercentage() : 0.0;
                String status = pct >= 40.0 ? "PASSED" : "FAILED";
                String dateStr = attempt.getCompletedAt() != null ? attempt.getCompletedAt().format(formatter) : "";

                row.createCell(0).setCellValue(studentName);
                row.createCell(1).setCellValue(studentEmail);
                row.createCell(2).setCellValue(attempt.getSubject() != null ? attempt.getSubject() : "Quiz");
                row.createCell(3).setCellValue(attempt.getScore() != null ? attempt.getScore() : 0);
                row.createCell(4).setCellValue(attempt.getTotalPoints() != null ? attempt.getTotalPoints() : 0);
                row.createCell(5).setCellValue(Math.round(pct) + "%");
                row.createCell(6).setCellValue(status);
                row.createCell(7).setCellValue(dateStr);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage());
        }
    }
}
