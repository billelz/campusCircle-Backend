package com.example.campusCircle.repository;

import com.example.campusCircle.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    // Custom query methods can be added here if needed
}
