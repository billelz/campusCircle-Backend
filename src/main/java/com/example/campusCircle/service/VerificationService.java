package com.example.campuscircle.service;

import com.example.campuscircle.model.Verification;
import com.example.campuscircle.repository.VerificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class VerificationService {

    private final VerificationRepository verificationRepository;

    public VerificationService(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }

    // Créer une nouvelle demande de vérification
    public Verification createVerification(Verification verification) {
        // Vérifier si l'utilisateur a déjà une vérification active
        if (isUserVerified(verification.getUserId())) {
            throw new RuntimeException("L'utilisateur est déjà vérifié");
        }
        return verificationRepository.save(verification);
    }

    // Récupérer toutes les vérifications d'un utilisateur
    public List<Verification> getVerificationsByUser(Long userId) {
        return verificationRepository.findByUserId(userId);
    }

    // Récupérer la vérification active d'un utilisateur
    public Verification getActiveVerification(Long userId) {
        return verificationRepository.findActiveVerificationByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Aucune vérification active trouvée pour l'utilisateur: " + userId));
    }

    // Récupérer les vérifications en attente
    public List<Verification> getPendingVerifications() {
        return verificationRepository.findPendingVerifications();
    }

    // Vérifier un utilisateur (marquer comme vérifié)
    public Verification verifyUser(Long verificationId) {
        Verification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Vérification non trouvée avec l'ID: " + verificationId));
        
        if (verification.getVerifiedAt() != null) {
            throw new RuntimeException("Cette vérification a déjà été approuvée");
        }
        
        verification.setVerifiedAt(LocalDateTime.now());
        return verificationRepository.save(verification);
    }

    // Vérifier si un utilisateur est vérifié
    public boolean isUserVerified(Long userId) {
        return verificationRepository.isUserVerified(userId, LocalDateTime.now());
    }

    // Récupérer les vérifications expirées
    public List<Verification> getExpiredVerifications() {
        return verificationRepository.findExpiredVerifications(LocalDateTime.now());
    }

    // Supprimer une vérification
    public void deleteVerification(Long id) {
        if (!verificationRepository.existsById(id)) {
            throw new RuntimeException("Vérification non trouvée avec l'ID: " + id);
        }
        verificationRepository.deleteById(id);
    }

    // Supprimer toutes les vérifications d'un utilisateur
    public void deleteUserVerifications(Long userId) {
        verificationRepository.deleteByUserId(userId);
    }

    // Nettoyer les vérifications expirées
    @Transactional
    public int cleanupExpiredVerifications() {
        List<Verification> expired = getExpiredVerifications();
        verificationRepository.deleteAll(expired);
        return expired.size();
    }

    // Récupérer les vérifications par méthode
    public List<Verification> getVerificationsByMethod(String method) {
        return verificationRepository.findByVerificationMethod(method);
    }
}