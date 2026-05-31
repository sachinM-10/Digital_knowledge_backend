package com.quizvault.controller;

import com.quizvault.dto.QuizDto;
import com.quizvault.dto.QuizQuestionDto;
import com.quizvault.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<QuizQuestionDto>> getQuestions(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuestionsByQuizId(id));
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<QuizQuestionDto> addQuestion(@PathVariable String id, @RequestBody QuizQuestionDto dto) {
        return ResponseEntity.ok(quizService.addQuestionToQuiz(id, dto));
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String id, @PathVariable String questionId) {
        quizService.deleteQuestion(questionId);
        return ResponseEntity.ok().build();
    }
}
