package com.example.campusCircle.controller;

import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.Ban;
import com.example.campusCircle.service.BanService;

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

    @GetMapping("/{id}")
    public Ban getOne(@PathVariable Long id) {
        return banService.getBan(id);
    }

    @GetMapping
    public List<Ban> getAll() {
        return banService.getAllBans();
    }

    @PutMapping("/{id}")
    public Ban update(@PathVariable Long id, @RequestBody Ban updated) {
        return banService.updateBan(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        banService.deleteBan(id);
    }
}