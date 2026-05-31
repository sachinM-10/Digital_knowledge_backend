package com.quizvault.repository;

import com.quizvault.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, String> {
    Optional<Certificate> findByAttemptId(String attemptId);
    List<Certificate> findByUserId(String userId);
}
