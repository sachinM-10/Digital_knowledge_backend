package com.quizvault.repository;

import com.quizvault.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, String> {
    List<QuizQuestion> findByQuizIdOrderBySortOrderAsc(String quizId);
    void deleteByQuizId(String quizId);
}
