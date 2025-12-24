package com.example.campusCircle.service;

import com.example.campusCircle.model.Karma;
import com.example.campusCircle.repository.KarmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KarmaService {

    private final KarmaRepository karmaRepository;

    public Karma createKarma(Karma karma) {
        return karmaRepository.save(karma);
    }

    public List<Karma> getAllKarma() {
        return karmaRepository.findAll();
    }

    public Karma getKarmaByUserId(Long userId) {
        return karmaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Karma not found for user: " + userId));
    }

    public Karma updateKarma(Long userId, Karma updatedKarma) {
        Karma existingKarma = getKarmaByUserId(userId);
        existingKarma.setKarmaScore(updatedKarma.getKarmaScore());
        existingKarma.setKarmaByChannel(updatedKarma.getKarmaByChannel());
        return karmaRepository.save(existingKarma);
    }

    public void deleteKarma(Long userId) {
        karmaRepository.deleteById(userId);
    }
}
