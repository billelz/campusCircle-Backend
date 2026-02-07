package com.example.campusCircle.repository;

import com.example.campusCircle.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findByToken(String token);

    Optional<Verification> findTopByUserIdOrderByExpiresAtDesc(Long userId);
}
