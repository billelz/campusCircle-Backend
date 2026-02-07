package com.example.campusCircle.controller;


import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.Verification;
import com.example.campusCircle.service.VerificationService;

import java.util.List;

@RestController
@RequestMapping("/api/Verifications")
public class VerificationController {

    private final VerificationService VerificationService;

    public VerificationController(VerificationService VerificationService) {
        this.VerificationService = VerificationService;
    }

    @PostMapping
    public Verification create(@RequestBody Verification Verification) {
        return VerificationService.createVerification(Verification);
    }

    @GetMapping("/{id}")
    public Verification getOne(@PathVariable Long id) {
        return VerificationService.getVerification(id);
    }

    @GetMapping
    public List<Verification> getAll() {
        return VerificationService.getAllVerifications();
    }

    @PutMapping("/{id}")
    public Verification update(@PathVariable Long id, @RequestBody Verification updated) {
        return VerificationService.updateVerification(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        VerificationService.deleteVerification(id);
    }
}
