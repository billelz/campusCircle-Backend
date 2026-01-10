package com.example.campusCircle.repository;

import com.example.campusCircle.model.Karma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KarmaRepository extends JpaRepository<Karma, Long> {
    
    Optional<Karma> findByUserId(Long userId);
    
    @Query("SELECT k FROM Karma k ORDER BY k.karmaScore DESC")
    List<Karma> findTopByKarmaScore();
    
    @Query("SELECT k FROM Karma k WHERE k.karmaScore >= :threshold ORDER BY k.karmaScore DESC")
    List<Karma> findByKarmaScoreGreaterThanEqual(@Param("threshold") Integer threshold);
    
    @Query("SELECT k FROM Karma k ORDER BY k.karmaScore DESC LIMIT :limit")
    List<Karma> findTopKarmaUsers(@Param("limit") int limit);
}
