package com.quizvault.repository;

import com.quizvault.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    List<Feedback> findAllByOrderByCreatedAtDesc();
}
