package com.quizvault.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizvault.dto.*;
import com.quizvault.entity.Attempt;
import com.quizvault.entity.Quiz;
import com.quizvault.entity.User;
import com.quizvault.repository.AttemptRepository;
import com.quizvault.repository.QuizRepository;
import com.quizvault.repository.UserRepository;
import com.quizvault.service.SubjectQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StudentQuizController {

    @Autowired
    private SubjectQuizService subjectQuizService;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/student/quizzes/{subject}")
    public ResponseEntity<?> getSubjectQuiz(@PathVariable String subject, Authentication authentication) {
        try {
            SubjectQuizResponseDto response = subjectQuizService.getQuestionsForSubject(authentication.getName(), subject);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/student/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody SubmitSubjectQuizRequest request, Authentication authentication) {
        try {
            SubmitSubjectQuizResponse response = subjectQuizService.submitQuiz(authentication.getName(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/student/auto-submit")
    public ResponseEntity<?> autoSubmitQuiz(@RequestBody SubmitSubjectQuizRequest request, Authentication authentication) {
        try {
            String email = authentication != null ? authentication.getName() : null;
            if (email == null) {
                // Return ok silently for unauthenticated beacon unload
                return ResponseEntity.ok(Collections.singletonMap("status", "received"));
            }
            SubmitSubjectQuizResponse response = subjectQuizService.submitQuiz(email, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.singletonMap("status", "received"));
        }
    }

    @GetMapping("/attempts")
    public ResponseEntity<List<AttemptDto>> getAttempts(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Attempt> attempts;
        if ("admin".equalsIgnoreCase(user.getRole())) {
            attempts = attemptRepository.findAll();
        } else {
            attempts = attemptRepository.findByUserIdOrderByCompletedAtDesc(user.getId());
        }

        Map<String, Quiz> quizMap = quizRepository.findAll().stream()
                .collect(Collectors.toMap(Quiz::getId, q -> q, (q1, q2) -> q1));

        List<AttemptDto> dtos = attempts.stream()
                .filter(a -> a.getCompletedAt() != null)
                .map(a -> {
                    Map<String, String> quizTitleObj = new HashMap<>();
                    String title = a.getSubject() != null ? (a.getSubject() + " Quiz") : "Quiz";
                    if (a.getQuizId() != null && quizMap.containsKey(a.getQuizId())) {
                        title = quizMap.get(a.getQuizId()).getTitle();
                    }
                    quizTitleObj.put("title", title);

                    return AttemptDto.builder()
                            ._id(a.getId())
                            .quizId(quizTitleObj)
                            .subject(a.getSubject())
                            .score(a.getScore())
                            .totalPoints(a.getTotalPoints())
                            .percentage(a.getPercentage())
                            .completedAt(a.getCompletedAt())
                            .build();
                }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/attempts")
    public ResponseEntity<AttemptDto> saveAttempt(@RequestBody Map<String, Object> body, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String quizId = (String) body.get("quizId");
        Number score = (Number) body.get("score");
        Number totalPoints = (Number) body.get("totalPoints");
        Number percentage = (Number) body.get("percentage");
        Object answers = body.get("answers");

        Attempt attempt = Attempt.builder()
                .userId(user.getId())
                .quizId(quizId)
                .score(score != null ? score.doubleValue() : 0.0)
                .totalPoints(totalPoints != null ? totalPoints.intValue() : 0)
                .percentage(percentage != null ? percentage.doubleValue() : 0.0)
                .completedAt(LocalDateTime.now())
                .reason("COMPLETED")
                .build();

        try {
            if (answers != null) attempt.setAnswersJson(objectMapper.writeValueAsString(answers));
        } catch (Exception e) {}

        attempt = attemptRepository.save(attempt);

        Map<String, String> quizTitleObj = new HashMap<>();
        quizTitleObj.put("title", "Quiz Attempt");

        AttemptDto dto = AttemptDto.builder()
                ._id(attempt.getId())
                .quizId(quizTitleObj)
                .score(attempt.getScore())
                .totalPoints(attempt.getTotalPoints())
                .percentage(attempt.getPercentage())
                .completedAt(attempt.getCompletedAt())
                .build();

        return ResponseEntity.ok(dto);
    }
}
