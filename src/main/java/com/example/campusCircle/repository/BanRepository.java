package com.example.campusCircle.repository;

import com.example.campusCircle.model.Ban;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BanRepository extends JpaRepository<Ban, Long> {

    List<Ban> findByUserId(Long userId);

    Optional<Ban> findByUserIdAndExpiresAtAfter(Long userId, LocalDateTime now);

    List<Ban> findByExpiresAtBefore(LocalDateTime now);
}