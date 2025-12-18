package com.example.campusCircle.controller;

import com.example.campusCircle.model.Karma;
import com.example.campusCircle.service.KarmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/karma")
@RequiredArgsConstructor
public class KarmaController {

    private final KarmaService karmaService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Karma> getKarmaByUserId(@PathVariable Long userId) {
        return karmaService.getKarmaByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
