package com.example.campusCircle.controller;


import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.University;
import com.example.campusCircle.service.UniversityService;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {

    private final UniversityService universityService;

    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @PostMapping
    public University create(@RequestBody University university) {
        return universityService.createUniversity(university);
    }

    @GetMapping("/{id}")
    public University getOne(@PathVariable Long id) {
        return universityService.getUniversity(id);
    }

    @GetMapping
    public List<University> getAll() {
        return universityService.getAllUniversities();
    }

    @PutMapping("/{id}")
    public University update(@PathVariable Long id, @RequestBody University updated) {
        return universityService.updateUniversity(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        universityService.deleteUniversity(id);
    }
}
