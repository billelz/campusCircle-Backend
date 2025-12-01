package com.example.campuscircle.service;

import com.example.campuscircle.model.Ban;
import com.example.campuscircle.repository.BanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BanService {

    private final BanRepository banRepository;

    public BanService(BanRepository banRepository) {
        this.banRepository = banRepository;
    }

    // Créer un nouveau ban
    public Ban createBan(Ban ban) {
        // Vérifier si l'utilisateur est déjà banni
        if (isUserBanned(ban.getUserId())) {
            throw new RuntimeException("L'utilisateur est déjà banni");
        }
        
        // Calculer la date d'expiration selon la durée
        if (!ban.getDuration().equals("PERMANENT")) {
            ban.setExpiresAt(calculateExpirationDate(ban.getDuration()));
        }
        
        return banRepository.save(ban);
    }

    // Récupérer tous les bans d'un utilisateur
    public List<Ban> getBansByUser(Long userId) {
        return banRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Récupérer le ban actif d'un utilisateur
    public Ban getActiveBan(Long userId) {
        return banRepository.findActiveBanByUserId(userId, LocalDateTime.now())
                .orElse(null);
    }

    // Récupérer tous les bans actifs
    public List<Ban> getAllActiveBans() {
        return banRepository.findAllActiveBans(LocalDateTime.now());
    }

    // Récupérer tous les bans
    public List<Ban> getAllBans() {
        return banRepository.findAll();
    }

    // Récupérer les bans créés par un modérateur
    public List<Ban> getBansByModerator(String bannedBy) {
        return banRepository.findByBannedBy(bannedBy);
    }

    // Vérifier si un utilisateur est banni
    public boolean isUserBanned(Long userId) {
        return banRepository.isUserBanned(userId, LocalDateTime.now());
    }

    // Révoquer un ban (le désactiver)
    public void revokeBan(Long banId) {
        Ban ban = banRepository.findById(banId)
                .orElseThrow(() -> new RuntimeException("Ban non trouvé avec l'ID: " + banId));
        
        ban.setIsActive(false);
        banRepository.save(ban);
    }

    // Révoquer tous les bans actifs d'un utilisateur
    public void revokeUserBans(Long userId) {
        Ban activeBan = getActiveBan(userId);
        if (activeBan != null) {
            activeBan.setIsActive(false);
            banRepository.save(activeBan);
        }
    }

    // Récupérer les bans permanents
    public List<Ban> getPermanentBans() {
        return banRepository.findPermanentBans();
    }

    // Compter les bans actifs
    public long countActiveBans() {
        return banRepository.countActiveBans(LocalDateTime.now());
    }

    // Nettoyer les bans expirés (marquer comme inactifs)
    @Transactional
    public int cleanupExpiredBans() {
        List<Ban> expiredBans = banRepository.findExpiredActiveBans(LocalDateTime.now());
        for (Ban ban : expiredBans) {
            ban.setIsActive(false);
        }
        banRepository.saveAll(expiredBans);
        return expiredBans.size();
    }

    // Calculer la date d'expiration selon la durée
    private LocalDateTime calculateExpirationDate(String duration) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (duration) {
            case "1_DAY":
                return now.plusDays(1);
            case "7_DAYS":
                return now.plusDays(7);
            case "30_DAYS":
                return now.plusDays(30);
            case "90_DAYS":
                return now.plusDays(90);
            case "1_YEAR":
                return now.plusYears(1);
            case "PERMANENT":
                return null;
            default:
                throw new RuntimeException("Durée de ban invalide: " + duration);
        }
    }

    // Mettre à jour un ban
    public Ban updateBan(Long banId, Ban updatedBan) {
        Ban existingBan = banRepository.findById(banId)
                .orElseThrow(() -> new RuntimeException("Ban non trouvé avec l'ID: " + banId));
        
        if (updatedBan.getReason() != null) {
            existingBan.setReason(updatedBan.getReason());
        }
        if (updatedBan.getDuration() != null) {
            existingBan.setDuration(updatedBan.getDuration());
            existingBan.setExpiresAt(calculateExpirationDate(updatedBan.getDuration()));
        }
        
        return banRepository.save(existingBan);
    }
}