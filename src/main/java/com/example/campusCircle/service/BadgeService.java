package com.example.campusCircle.service;

import com.example.campusCircle.model.Badge;
import com.example.campusCircle.model.Karma;
import com.example.campusCircle.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private KarmaService karmaService;

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Optional<Badge> getBadgeById(Long id) {
        return badgeRepository.findById(id);
    }

    public List<Badge> getUserBadges(Long userId) {
        return badgeRepository.findByUserId(userId);
    }

    public boolean hasBadge(Long userId, Badge.BadgeType badgeType) {
        return badgeRepository.existsByUserIdAndBadgeType(userId, badgeType);
    }

    @Transactional
    public Badge awardBadge(Long userId, Badge.BadgeType badgeType, Long channelId) {
        // Check if user already has this badge
        if (badgeRepository.existsByUserIdAndBadgeType(userId, badgeType)) {
            return badgeRepository.findByUserIdAndBadgeType(userId, badgeType).orElse(null);
        }

        Badge badge = new Badge();
        badge.setUserId(userId);
        badge.setBadgeType(badgeType);
        badge.setChannelId(channelId);
        return badgeRepository.save(badge);
    }

    @Transactional
    public void revokeBadge(Long userId, Badge.BadgeType badgeType) {
        badgeRepository.findByUserIdAndBadgeType(userId, badgeType)
                .ifPresent(badgeRepository::delete);
    }

    @Transactional
    public void checkAndAwardKarmaBadges(Long userId) {
        Optional<Karma> karmaOpt = karmaService.getKarmaByUserId(userId);
        if (karmaOpt.isEmpty()) return;

        Karma karma = karmaOpt.get();

        // Top Contributor badge (top 1% - let's say 10000+ karma)
        if (karma.getKarmaScore() >= 10000) {
            awardBadge(userId, Badge.BadgeType.TOP_CONTRIBUTOR, null);
        }

        // Academic badge (high karma in academic channels)
        // This would need channel category mapping - simplified here
        if (karma.getKarmaScore() >= 500) {
            awardBadge(userId, Badge.BadgeType.ACADEMIC, null);
        }
    }

    public void awardVerifiedStudentBadge(Long userId) {
        awardBadge(userId, Badge.BadgeType.VERIFIED_STUDENT, null);
    }

    public void awardModeratorBadge(Long userId) {
        awardBadge(userId, Badge.BadgeType.MODERATOR, null);
    }

    public void awardPioneerBadge(Long userId) {
        awardBadge(userId, Badge.BadgeType.PIONEER, null);
    }
}
