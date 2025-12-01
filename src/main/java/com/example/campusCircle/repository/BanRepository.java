package com.example.campuscircle.repository;

import com.example.campuscircle.model.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BanRepository extends JpaRepository<Ban, Long> {

    // Trouver tous les bans d'un utilisateur
    List<Ban> findByUserId(Long userId);

    // Trouver le ban actif d'un utilisateur
    @Query("SELECT b FROM Ban b WHERE b.userId = :userId AND b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now)")
    Optional<Ban> findActiveBanByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Trouver tous les bans actifs
    @Query("SELECT b FROM Ban b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now)")
    List<Ban> findAllActiveBans(@Param("now") LocalDateTime now);

    // Trouver les bans expirés qui sont encore marqués comme actifs
    @Query("SELECT b FROM Ban b WHERE b.isActive = true AND b.expiresAt IS NOT NULL AND b.expiresAt < :now")
    List<Ban> findExpiredActiveBans(@Param("now") LocalDateTime now);

    // Trouver tous les bans créés par un modérateur
    List<Ban> findByBannedBy(String bannedBy);

    // Vérifier si un utilisateur est actuellement banni
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Ban b WHERE b.userId = :userId AND b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now)")
    boolean isUserBanned(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Trouver les bans permanents
    @Query("SELECT b FROM Ban b WHERE b.duration = 'PERMANENT' AND b.isActive = true")
    List<Ban> findPermanentBans();

    // Compter le nombre total de bans actifs
    @Query("SELECT COUNT(b) FROM Ban b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now)")
    long countActiveBans(@Param("now") LocalDateTime now);

    // Trouver tous les bans d'un utilisateur (actifs et inactifs)
    List<Ban> findByUserIdOrderByCreatedAtDesc(Long userId);
}