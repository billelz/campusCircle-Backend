package com.example.campusCircle.service;

import com.example.campusCircle.model.Verification;
import com.example.campusCircle.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VerificationService {

    private final VerificationRepository verificationRepository;

    public VerificationService(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }

    // Existing methods
    public Verification startVerification(Long userId) {
        Verification verification = Verification.builder()
                .userId(userId)
                .verificationMethod("UNIVERSITY_EMAIL")
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        return verificationRepository.save(verification);
    }

    public Verification confirmVerification(String token) {
        Verification verification = verificationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        verification.setVerifiedAt(LocalDateTime.now());
        return verificationRepository.save(verification);
    }

    // Add these CRUD methods
    public Verification createVerification(Verification verification) {
        return verificationRepository.save(verification);
    }

    public Verification getVerification(Long id) {
        return verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));
    }

    public List<Verification> getAllVerifications() {
        return verificationRepository.findAll();
    }

    public Verification updateVerification(Long id, Verification updated) {
        Verification existing = getVerification(id);
        
        if (updated.getUserId() != null) {
            existing.setUserId(updated.getUserId());
        }
        if (updated.getVerificationMethod() != null) {
            existing.setVerificationMethod(updated.getVerificationMethod());
        }
        if (updated.getToken() != null) {
            existing.setToken(updated.getToken());
        }
        if (updated.getVerifiedAt() != null) {
            existing.setVerifiedAt(updated.getVerifiedAt());
        }
        if (updated.getExpiresAt() != null) {
            existing.setExpiresAt(updated.getExpiresAt());
        }
        
        return verificationRepository.save(existing);
    }

    public void deleteVerification(Long id) {
        verificationRepository.deleteById(id);
    }
}