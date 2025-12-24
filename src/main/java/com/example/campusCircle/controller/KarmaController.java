package com.example.campusCircle.controller;

import com.example.campusCircle.model.Karma;
import com.example.campusCircle.service.KarmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/karma")
@RequiredArgsConstructor
public class KarmaController {

    private final KarmaService karmaService;

    @PostMapping
    public Karma createKarma(@RequestBody Karma karma) {
        return karmaService.createKarma(karma);
    }

    @GetMapping
    public List<Karma> getAllKarma() {
        return karmaService.getAllKarma();
    }

    @GetMapping("/user/{userId}")
    public Karma getKarmaByUserId(@PathVariable Long userId) {
        return karmaService.getKarmaByUserId(userId);
    }

    @PutMapping("/user/{userId}")
    public Karma updateKarma(@PathVariable Long userId, @RequestBody Karma karma) {
        return karmaService.updateKarma(userId, karma);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteKarma(@PathVariable Long userId) {
        karmaService.deleteKarma(userId);
    }
}
