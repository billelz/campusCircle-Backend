package com.example.campusCircle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.campusCircle.model.University;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    
    Optional<University> findByDomain(String domain);
    
    boolean existsByDomain(String domain);
}
