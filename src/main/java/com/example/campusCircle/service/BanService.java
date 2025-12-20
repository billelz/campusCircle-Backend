package com.example.campusCircle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Ban;
import com.example.campusCircle.repository.BanRepository;

@Service
public class BanService {

    private final BanRepository banRepository;

    public BanService(BanRepository banRepository) {
        this.banRepository = banRepository;
    }

    public Ban createBan(Ban ban) {
        return banRepository.save(ban);
    }

    public Ban getBan(Long id) {
        return banRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ban not found"));
    }

    public List<Ban> getAllBans() {
        return banRepository.findAll();
    }

    public Ban updateBan(Long id, Ban updated) {
        Ban existing = getBan(id);

        existing.setUserId(updated.getUserId());
        existing.setBannedBy(updated.getBannedBy());
        existing.setReason(updated.getReason());
        existing.setDuration(updated.getDuration());
        existing.setExpiresAt(updated.getExpiresAt());

        return banRepository.save(existing);
    }

    public void deleteBan(Long id) {
        banRepository.deleteById(id);
    }
}