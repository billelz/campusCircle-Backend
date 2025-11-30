package com.example.campusCircle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.University;
import com.example.campusCircle.repository.UniversityRepository;

@Service
public class UniversityService {

    private final UniversityRepository universityRepository;

    public UniversityService(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    public University createUniversity(University university) {
        return universityRepository.save(university);
    }

    public University getUniversity(Long id) {
        return universityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("University not found"));
    }

    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public University updateUniversity(Long id, University updated) {
        University existing = getUniversity(id);

        existing.setName(updated.getName());
        existing.setDomain(updated.getDomain());
        existing.setLocation(updated.getLocation());
        existing.setStudentCount(updated.getStudentCount());
        existing.setActiveStatus(updated.getActiveStatus());

        return universityRepository.save(existing);
    }

    public void deleteUniversity(Long id) {
        universityRepository.deleteById(id);
    }
}
