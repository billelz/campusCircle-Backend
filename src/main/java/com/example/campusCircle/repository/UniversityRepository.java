package com.example.campusCircle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.campusCircle.model.University;

public interface UniversityRepository extends JpaRepository<University, Long> { }
