package com.quizvault.repository;

import com.quizvault.entity.SubjectQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectQuestionRepository extends JpaRepository<SubjectQuestion, String> {
    List<SubjectQuestion> findBySubject(String subject);
    List<SubjectQuestion> findBySubjectAndBank(String subject, Integer bank);
    List<SubjectQuestion> findByBank(Integer bank);

    long countBySubject(String subject);

    @Query("SELECT COUNT(DISTINCT s.bank) FROM SubjectQuestion s WHERE s.subject = :subject")
    long countDistinctBanksBySubject(@Param("subject") String subject);
}
