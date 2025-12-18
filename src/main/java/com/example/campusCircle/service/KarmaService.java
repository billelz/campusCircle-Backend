package com.example.campusCircle.service;

import com.example.campusCircle.model.Karma;
import com.example.campusCircle.repository.KarmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KarmaService {

    private final KarmaRepository karmaRepository;

    public Optional<Karma> getKarmaByUserId(Long userId) {
        return karmaRepository.findById(userId);
    }

    // Additional business logic for updating karma can be added here
}
