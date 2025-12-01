package com.example.campuscircle.repository;

import com.example.campuscircle.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // Trouver toutes les vérifications d'un utilisateur
    List<Verification> findByUserId(Long userId);

    // Trouver la vérification active d'un utilisateur
    @Query("SELECT v FROM Verification v WHERE v.userId = :userId AND v.verifiedAt IS NOT NULL AND (v.expiresAt IS NULL OR v.expiresAt > :now)")
    Optional<Verification> findActiveVerificationByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Trouver les vérifications par méthode
    List<Verification> findByVerificationMethod(String verificationMethod);

    // Trouver les vérifications expirées
    @Query("SELECT v FROM Verification v WHERE v.expiresAt IS NOT NULL AND v.expiresAt < :now")
    List<Verification> findExpiredVerifications(@Param("now") LocalDateTime now);

    // Vérifier si un utilisateur est vérifié
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Verification v WHERE v.userId = :userId AND v.verifiedAt IS NOT NULL AND (v.expiresAt IS NULL OR v.expiresAt > :now)")
    boolean isUserVerified(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Trouver les vérifications en attente (non vérifiées)
    @Query("SELECT v FROM Verification v WHERE v.verifiedAt IS NULL")
    List<Verification> findPendingVerifications();

    // Supprimer toutes les vérifications d'un utilisateur
    void deleteByUserId(Long userId);
}