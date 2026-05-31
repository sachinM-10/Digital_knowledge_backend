package com.quizvault.service;

import com.quizvault.dto.FeedbackDto;
import com.quizvault.entity.Feedback;
import com.quizvault.entity.SubjectQuestion;
import com.quizvault.entity.User;
import com.quizvault.repository.FeedbackRepository;
import com.quizvault.repository.SubjectQuestionRepository;
import com.quizvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectQuestionRepository subjectQuestionRepository;

    public void submitFeedback(String email, Feedback request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        request.setUserId(user.getId());
        request.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(request);
    }

    public List<FeedbackDto> getAllFeedback() {
        List<Feedback> feedbacks = feedbackRepository.findAllByOrderByCreatedAtDesc();

        Map<String, User> userMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u, (u1, u2) -> u1));

        Map<String, SubjectQuestion> questionMap = subjectQuestionRepository.findAll().stream()
                .collect(Collectors.toMap(SubjectQuestion::getId, q -> q, (q1, q2) -> q1));

        return feedbacks.stream().map(f -> {
            User u = userMap.get(f.getUserId());
            Map<String, String> userObj = new HashMap<>();
            if (u != null) {
                userObj.put("name", u.getDisplayName());
                userObj.put("username", u.getDisplayName());
                userObj.put("email", u.getEmail());
            }

            Map<String, String> qObj = null;
            if (f.getReportedQuestionId() != null && questionMap.containsKey(f.getReportedQuestionId())) {
                qObj = new HashMap<>();
                qObj.put("question", questionMap.get(f.getReportedQuestionId()).getQuestion());
            }

            return FeedbackDto.builder()
                    ._id(f.getId())
                    .userId(userObj)
                    .subject(f.getSubject())
                    .ratingDifficulty(f.getRatingDifficulty())
                    .ratingQuality(f.getRatingQuality())
                    .suggestionText(f.getSuggestionText())
                    .reportedQuestionId(qObj)
                    .reportText(f.getReportText())
                    .createdAt(f.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }
}
