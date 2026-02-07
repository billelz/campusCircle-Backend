package com.example.campusCircle.controller;


import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.ModerationActions;
import com.example.campusCircle.service.ModerationActionsService;
import java.util.List;

@RestController
@RequestMapping("/api/moderation-actions")
public class ModerationActionsController {

    private final ModerationActionsService moderationActionsService;

    public ModerationActionsController(ModerationActionsService moderationActionsService) {
        this.moderationActionsService = moderationActionsService;
    }

    @PostMapping
    public ModerationActions create(@RequestBody ModerationActions moderationAction) {
        return moderationActionsService.createModerationAction(moderationAction);
    }

    @GetMapping("/{id}")
    public ModerationActions get(@PathVariable Long id) {
        return moderationActionsService.getModerationAction(id);
    }

    @GetMapping
    public List<ModerationActions> getAll() {
        return moderationActionsService.getAllModerationActions();
    }

    @PutMapping("/{id}")
    public ModerationActions update(@PathVariable Long id, @RequestBody ModerationActions moderationAction) {
        return moderationActionsService.updateModerationAction(id, moderationAction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        moderationActionsService.deleteModerationAction(id);
    }
}
