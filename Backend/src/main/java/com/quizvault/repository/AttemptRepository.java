package com.quizvault.repository;

import com.quizvault.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, String> {
    List<Attempt> findByUserIdOrderByCompletedAtDesc(String userId);
    List<Attempt> findByUserIdAndSubject(String userId, String subject);
    List<Attempt> findBySubject(String subject);

    long countBySubject(String subject);

    @Query("SELECT a FROM Attempt a WHERE (:subject IS NULL OR a.subject = :subject) " +
           "AND (:startDate IS NULL OR a.completedAt >= :startDate) " +
           "AND (:endDate IS NULL OR a.completedAt <= :endDate) " +
           "ORDER BY a.completedAt DESC")
    List<Attempt> filterAttempts(
            @Param("subject") String subject,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
