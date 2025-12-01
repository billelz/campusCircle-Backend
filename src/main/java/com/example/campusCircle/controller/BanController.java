package com.example.campuscircle.controller;

import org.springframework.web.bind.annotation.*;
import com.example.campuscircle.model.Ban;
import com.example.campuscircle.service.BanService;

import java.util.List;

@RestController
@RequestMapping("/api/bans")
public class BanController {

    private final BanService banService;

    public BanController(BanService banService) {
        this.banService = banService;
    }

    @PostMapping
    public Ban create(@RequestBody Ban ban) {
        return banService.createBan(ban);
    }

    @GetMapping("/user/{userId}")
    public List<Ban> getByUser(@PathVariable Long userId) {
        return banService.getBansByUser(userId);
    }

    @GetMapping("/active/{userId}")
    public Ban getActiveBan(@PathVariable Long userId) {
        return banService.getActiveBan(userId);
    }

    @DeleteMapping("/{id}")
    public void revoke(@PathVariable Long id) {
        banService.revokeBan(id);
    }

    @GetMapping
    public List<Ban> getAll() {
        return banService.getAllBans();
    }
}