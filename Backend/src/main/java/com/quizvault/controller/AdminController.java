package com.quizvault.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizvault.dto.*;
import com.quizvault.entity.Attempt;
import com.quizvault.entity.SubjectQuestion;
import com.quizvault.entity.User;
import com.quizvault.repository.*;
import com.quizvault.service.SeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private SubjectQuestionRepository subjectQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SeedService seedService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/admin/questions")
    public ResponseEntity<List<SubjectQuestionDto>> getQuestions(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Integer bank) {

        List<SubjectQuestion> questions;
        if (subject != null && bank != null) {
            questions = subjectQuestionRepository.findBySubjectAndBank(subject, bank);
        } else if (subject != null) {
            questions = subjectQuestionRepository.findBySubject(subject);
        } else if (bank != null) {
            questions = subjectQuestionRepository.findByBank(bank);
        } else {
            questions = subjectQuestionRepository.findAll();
        }

        List<SubjectQuestionDto> dtos = questions.stream().map(q -> {
            List<String> options = Collections.emptyList();
            try {
                options = objectMapper.readValue(q.getOptionsJson(), new TypeReference<List<String>>() {});
            } catch (Exception e) {}

            return SubjectQuestionDto.builder()
                    ._id(q.getId())
                    .subject(q.getSubject())
                    .bank(q.getBank())
                    .question(q.getQuestion())
                    .options(options)
                    .correctAnswer(q.getCorrectAnswer())
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/admin/question")
    public ResponseEntity<SubjectQuestionDto> createQuestion(@RequestBody SubjectQuestionDto dto) {
        try {
            SubjectQuestion sq = SubjectQuestion.builder()
                    .subject(dto.getSubject())
                    .bank(dto.getBank() != null ? dto.getBank() : 1)
                    .question(dto.getQuestion())
                    .optionsJson(objectMapper.writeValueAsString(dto.getOptions()))
                    .correctAnswer(dto.getCorrectAnswer())
                    .build();

            sq = subjectQuestionRepository.save(sq);
            dto.set_id(sq.getId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            throw new RuntimeException("Error saving question: " + e.getMessage());
        }
    }

    @PutMapping("/admin/question/{id}")
    public ResponseEntity<SubjectQuestionDto> updateQuestion(@PathVariable String id, @RequestBody SubjectQuestionDto dto) {
        SubjectQuestion sq = subjectQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try {
            sq.setSubject(dto.getSubject());
            sq.setBank(dto.getBank() != null ? dto.getBank() : 1);
            sq.setQuestion(dto.getQuestion());
            sq.setOptionsJson(objectMapper.writeValueAsString(dto.getOptions()));
            sq.setCorrectAnswer(dto.getCorrectAnswer());

            sq = subjectQuestionRepository.save(sq);
            dto.set_id(sq.getId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            throw new RuntimeException("Error updating question: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/question/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String id) {
        subjectQuestionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seed")
    public ResponseEntity<?> seedDatabase() {
        seedService.seedInitialData();
        return ResponseEntity.ok(Collections.singletonMap("message", "Database seeded successfully"));
    }

    @GetMapping("/admin/dashboard/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        long totalStudents = userRepository.countByRole("student");
        long totalQuestions = subjectQuestionRepository.count();
        long totalAttempts = attemptRepository.findAll().stream().filter(a -> a.getCompletedAt() != null).count();
        long totalQuizzes = quizRepository.count() + 3; // 3 core subjects (C, Python, Java)

        return ResponseEntity.ok(DashboardStatsDto.builder()
                .totalStudents(totalStudents)
                .totalQuestions(totalQuestions)
                .totalAttempts(totalAttempts)
                .totalQuizzes(totalQuizzes)
                .build());
    }

    @GetMapping("/admin/subjects/stats")
    public ResponseEntity<List<SubjectStatDto>> getSubjectStats() {
        List<String> subjects = Arrays.asList("C", "Python", "Java");
        List<SubjectStatDto> list = new ArrayList<>();

        for (String sub : subjects) {
            long totalQuestions = subjectQuestionRepository.countBySubject(sub);
            long banksCount = subjectQuestionRepository.countDistinctBanksBySubject(sub);
            if (banksCount == 0 && totalQuestions > 0) banksCount = 1;
            long attemptsCount = attemptRepository.countBySubject(sub);

            list.add(SubjectStatDto.builder()
                    .subject(sub)
                    .totalQuestions(totalQuestions)
                    .banksCount(banksCount)
                    .attemptsCount(attemptsCount)
                    .build());
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/admin/students/results")
    public ResponseEntity<List<PerformanceResultDto>> getStudentResults() {
        List<Attempt> completedAttempts = attemptRepository.findAll().stream()
                .filter(a -> a.getCompletedAt() != null)
                .collect(Collectors.toList());

        Map<String, User> userMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u, (u1, u2) -> u1));

        // Group attempts by userId + subject
        Map<String, List<Attempt>> grouped = completedAttempts.stream()
                .collect(Collectors.groupingBy(a -> a.getUserId() + "::" + (a.getSubject() != null ? a.getSubject() : "Quiz")));

        List<PerformanceResultDto> results = new ArrayList<>();

        for (Map.Entry<String, List<Attempt>> entry : grouped.entrySet()) {
            String[] parts = entry.getKey().split("::");
            String userId = parts[0];
            String subject = parts[1];

            User user = userMap.get(userId);
            if (user == null) continue;

            List<Attempt> list = entry.getValue();
            list.sort(Comparator.comparing(Attempt::getCompletedAt));

            Attempt latest = list.get(list.size() - 1);
            Attempt highest = list.stream().max(Comparator.comparing(a -> a.getScore() != null ? a.getScore() : 0.0)).orElse(latest);

            results.add(PerformanceResultDto.builder()
                    .studentName(user.getDisplayName())
                    .studentEmail(user.getEmail())
                    .subject(subject)
                    .numberOfAttempts(list.size())
                    .highestScore(highest.getScore() != null ? highest.getScore() : 0.0)
                    .latestScore(latest.getScore() != null ? latest.getScore() : 0.0)
                    .latestTotal(latest.getTotalPoints() != null ? latest.getTotalPoints() : 10)
                    .highestTotal(highest.getTotalPoints() != null ? highest.getTotalPoints() : 10)
                    .lastAttemptDate(latest.getCompletedAt())
                    .build());
        }

        return ResponseEntity.ok(results);
    }
}
