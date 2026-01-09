package com.example.campusCircle.service;

import com.example.campusCircle.model.Karma;
import com.example.campusCircle.repository.KarmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KarmaService {

    @Autowired
    private KarmaRepository karmaRepository;

    // Karma thresholds for features
    public static final int THRESHOLD_CREATE_POLLS = 100;
    public static final int THRESHOLD_CREATE_CHANNELS = 500;
    public static final int THRESHOLD_MODERATOR_ELIGIBLE = 1000;

    public List<Karma> getAllKarma() {
        return karmaRepository.findAll();
    }

    public Optional<Karma> getKarmaById(Long id) {
        return karmaRepository.findById(id);
    }

    public Optional<Karma> getKarmaByUserId(Long userId) {
        return karmaRepository.findByUserId(userId);
    }

    public int getUserKarmaScore(Long userId) {
        return karmaRepository.findByUserId(userId)
                .map(Karma::getKarmaScore)
                .orElse(0);
    }

    public List<Karma> getTopKarmaUsers(int limit) {
        return karmaRepository.findTopKarmaUsers(limit);
    }

    public List<Karma> getLeaderboard() {
        return karmaRepository.findTopByKarmaScore();
    }

    @Transactional
    public Karma getOrCreateKarma(Long userId) {
        return karmaRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Karma karma = new Karma();
                    karma.setUserId(userId);
                    karma.setKarmaScore(0);
                    karma.setPostKarma(0);
                    karma.setCommentKarma(0);
                    return karmaRepository.save(karma);
                });
    }

    @Transactional
    public void addPostKarma(Long userId, int amount, Long channelId) {
        Karma karma = getOrCreateKarma(userId);
        karma.addPostKarma(amount);
        if (channelId != null) {
            karma.addChannelKarma(channelId, amount);
        }
        karmaRepository.save(karma);
    }

    @Transactional
    public void addCommentKarma(Long userId, int amount, Long channelId) {
        Karma karma = getOrCreateKarma(userId);
        karma.addCommentKarma(amount);
        if (channelId != null) {
            karma.addChannelKarma(channelId, amount);
        }
        karmaRepository.save(karma);
    }

    @Transactional
    public void applyModerationPenalty(Long userId, int penalty) {
        Karma karma = getOrCreateKarma(userId);
        karma.addPostKarma(-penalty);
        karmaRepository.save(karma);
    }

    public boolean canCreatePolls(Long userId) {
        return getUserKarmaScore(userId) >= THRESHOLD_CREATE_POLLS;
    }

    public boolean canCreateChannels(Long userId) {
        return getUserKarmaScore(userId) >= THRESHOLD_CREATE_CHANNELS;
    }

    public boolean isModeratorEligible(Long userId) {
        return getUserKarmaScore(userId) >= THRESHOLD_MODERATOR_ELIGIBLE;
    }
}
