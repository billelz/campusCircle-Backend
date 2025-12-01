package com.example.campuscircle.controller;

import org.springframework.web.bind.annotation.*;
import com.example.campuscircle.model.Verification;
import com.example.campuscircle.service.VerificationService;

import java.util.List;

@RestController
@RequestMapping("/api/verifications")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping
    public Verification create(@RequestBody Verification verification) {
        return verificationService.createVerification(verification);
    }

    @GetMapping("/user/{userId}")
    public List<Verification> getByUser(@PathVariable Long userId) {
        return verificationService.getVerificationsByUser(userId);
    }

    @PutMapping("/{id}/verify")
    public Verification verify(@PathVariable Long id) {
        return verificationService.verifyUser(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        verificationService.deleteVerification(id);
    }
}