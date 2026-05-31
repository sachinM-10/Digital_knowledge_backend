package com.quizvault.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizvault.dto.*;
import com.quizvault.entity.Attempt;
import com.quizvault.entity.SubjectQuestion;
import com.quizvault.entity.User;
import com.quizvault.repository.AttemptRepository;
import com.quizvault.repository.SubjectQuestionRepository;
import com.quizvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubjectQuizService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectQuestionRepository subjectQuestionRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private CertificateService certificateService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SubjectQuizResponseDto getQuestionsForSubject(String email, String subject) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Attempt> completedAttempts = attemptRepository.findByUserIdAndSubject(user.getId(), subject)
                .stream()
                .filter(a -> a.getCompletedAt() != null)
                .collect(Collectors.toList());

        int nextBank = completedAttempts.size() + 1;
        if (nextBank > 4) {
            throw new RuntimeException("Maximum attempt limit (4) reached for " + subject + ". No further attempts allowed.");
        }

        List<SubjectQuestion> questions = subjectQuestionRepository.findBySubjectAndBank(subject, nextBank);
        if (questions.isEmpty()) {
            questions = subjectQuestionRepository.findBySubject(subject);
        }

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions configured for " + subject);
        }

        List<SubjectQuestionDto> dtos = questions.stream().map(q -> {
            List<String> options = Collections.emptyList();
            try {
                options = objectMapper.readValue(q.getOptionsJson(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                // fallback
            }
            return SubjectQuestionDto.builder()
                    ._id(q.getId())
                    .subject(q.getSubject())
                    .bank(q.getBank())
                    .question(q.getQuestion())
                    .options(options)
                    .build();
        }).collect(Collectors.toList());

        // Register initial IN_PROGRESS attempt
        Attempt attempt = Attempt.builder()
                .userId(user.getId())
                .subject(subject)
                .reason("IN_PROGRESS")
                .build();
        attempt = attemptRepository.save(attempt);

        return new SubjectQuizResponseDto(dtos, attempt.getId());
    }

    public SubmitSubjectQuizResponse submitQuiz(String email, SubmitSubjectQuizRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String subject = request.getSubject();
        Map<String, String> userAnswers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();

        // 1. Get the current bank based on prior completed attempts for this user and subject
        List<Attempt> completedAttempts = attemptRepository.findByUserIdAndSubject(user.getId(), subject)
                .stream()
                .filter(a -> a.getCompletedAt() != null)
                .collect(Collectors.toList());
        int currentBank = completedAttempts.size() + 1;
        if (currentBank > 4) {
            currentBank = 4;
        }

        // 2. Fetch the questions belonging ONLY to this bank
        List<SubjectQuestion> bankQuestions = subjectQuestionRepository.findBySubjectAndBank(subject, currentBank);
        if (bankQuestions.isEmpty()) {
            // Fallback to all questions if none exist for that bank
            bankQuestions = subjectQuestionRepository.findBySubject(subject);
        }

        Map<String, SubjectQuestion> questionMap = bankQuestions.stream()
                .collect(Collectors.toMap(SubjectQuestion::getId, q -> q, (q1, q2) -> q1));

        int score = 0;
        int correctCount = 0;
        int wrongCount = 0;
        int unattemptedCount = 0;

        List<QuestionReviewDto> reviewList = new ArrayList<>();
        Set<String> evaluatedIds = new HashSet<>();

        // 1. Evaluate questions that the user answered
        for (SubjectQuestion sq : bankQuestions) {
            String qId = sq.getId();
            evaluatedIds.add(qId);

            String userAnswer = userAnswers.get(qId);
            String correctAnswer = sq.getCorrectAnswer() != null ? sq.getCorrectAnswer() : "";

            if (userAnswer == null || userAnswer.trim().isEmpty()) {
                unattemptedCount++;
                reviewList.add(QuestionReviewDto.builder()
                        .questionId(qId)
                        .question(sq.getQuestion())
                        .userAnswer("Unattempted")
                        .correctAnswer(correctAnswer)
                        .isCorrect(false)
                        .build());
            } else {
                boolean isCorrect = correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
                if (isCorrect) {
                    score++;
                    correctCount++;
                } else {
                    wrongCount++;
                }
                reviewList.add(QuestionReviewDto.builder()
                        .questionId(qId)
                        .question(sq.getQuestion())
                        .userAnswer(userAnswer)
                        .correctAnswer(correctAnswer)
                        .isCorrect(isCorrect)
                        .build());
            }
        }

        int total = reviewList.size();
        if (total == 0) total = 1;
        double percentage = ((double) score / total) * 100.0;

        Attempt attempt = null;
        if (request.getAttemptId() != null) {
            Optional<Attempt> opt = attemptRepository.findById(request.getAttemptId());
            if (opt.isPresent()) attempt = opt.get();
        }

        if (attempt == null) {
            attempt = Attempt.builder()
                    .userId(user.getId())
                    .subject(subject)
                    .build();
        }

        attempt.setScore((double) score);
        attempt.setTotalPoints(total);
        attempt.setPercentage(percentage);
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setReason(request.getReason() != null ? request.getReason() : "SUBMITTED");
        try {
            attempt.setAnswersJson(objectMapper.writeValueAsString(userAnswers));
        } catch (Exception e) {}

        attempt = attemptRepository.save(attempt);

        String certId = null;
        if (percentage >= 40.0) {
            certId = certificateService.generateCertificate(user.getId(), user.getDisplayName(), subject, score + "/" + total, percentage, attempt.getId());
        }

        return SubmitSubjectQuizResponse.builder()
                .score(score)
                .total(total)
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .unattemptedCount(unattemptedCount)
                .results(reviewList)
                .attemptId(attempt.getId())
                .subject(subject)
                .certId(certId)
                .build();
    }
}
