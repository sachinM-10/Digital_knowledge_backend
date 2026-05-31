package com.quizvault.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizvault.dto.QuizDto;
import com.quizvault.dto.QuizQuestionDto;
import com.quizvault.entity.Quiz;
import com.quizvault.entity.QuizQuestion;
import com.quizvault.repository.QuizQuestionRepository;
import com.quizvault.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<QuizDto> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream().map(q -> {
            List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderBySortOrderAsc(q.getId());
            List<QuizQuestionDto> questionDtos = mapQuestions(questions);
            return QuizDto.builder()
                    ._id(q.getId())
                    .title(q.getTitle())
                    .description(q.getDescription())
                    .category(q.getCategory())
                    .timeLimitMinutes(q.getTimeLimitMinutes())
                    .questions(questionDtos)
                    .build();
        }).collect(Collectors.toList());
    }

    public QuizDto getQuizById(String id) {
        Quiz q = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderBySortOrderAsc(q.getId());
        return QuizDto.builder()
                ._id(q.getId())
                .title(q.getTitle())
                .description(q.getDescription())
                .category(q.getCategory())
                .timeLimitMinutes(q.getTimeLimitMinutes())
                .questions(mapQuestions(questions))
                .build();
    }

    public List<QuizQuestionDto> getQuestionsByQuizId(String quizId) {
        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderBySortOrderAsc(quizId);
        return mapQuestions(questions);
    }

    public QuizQuestionDto addQuestionToQuiz(String quizId, QuizQuestionDto dto) {
        try {
            QuizQuestion qq = QuizQuestion.builder()
                    .quizId(quizId)
                    .questionText(dto.getQuestionText())
                    .optionsJson(objectMapper.writeValueAsString(dto.getOptions()))
                    .correctAnswer(dto.getCorrectAnswer())
                    .points(dto.getPoints() != null ? dto.getPoints() : 1)
                    .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 1)
                    .build();

            qq = quizQuestionRepository.save(qq);

            return QuizQuestionDto.builder()
                    ._id(qq.getId())
                    .quizId(qq.getQuizId())
                    .questionText(qq.getQuestionText())
                    .options(dto.getOptions())
                    .correctAnswer(qq.getCorrectAnswer())
                    .points(qq.getPoints())
                    .sortOrder(qq.getSortOrder())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error adding question: " + e.getMessage());
        }
    }

    public void deleteQuestion(String questionId) {
        quizQuestionRepository.deleteById(questionId);
    }

    private List<QuizQuestionDto> mapQuestions(List<QuizQuestion> questions) {
        return questions.stream().map(qq -> {
            List<String> options = Collections.emptyList();
            try {
                options = objectMapper.readValue(qq.getOptionsJson(), new TypeReference<List<String>>() {});
            } catch (Exception e) {}

            return QuizQuestionDto.builder()
                    ._id(qq.getId())
                    .quizId(qq.getQuizId())
                    .questionText(qq.getQuestionText())
                    .options(options)
                    .correctAnswer(qq.getCorrectAnswer())
                    .points(qq.getPoints())
                    .sortOrder(qq.getSortOrder())
                    .build();
        }).collect(Collectors.toList());
    }
}
