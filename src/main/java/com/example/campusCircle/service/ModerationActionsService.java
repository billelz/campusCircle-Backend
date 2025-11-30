package com.example.campusCircle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.ModerationActions;
import com.example.campusCircle.repository.ModerationActionsRepository;

@Service
public class ModerationActionsService {

    private final ModerationActionsRepository moderationActionsRepository;

    public ModerationActionsService(ModerationActionsRepository moderationActionsRepository) {
        this.moderationActionsRepository = moderationActionsRepository;
    }

    public ModerationActions createModerationAction(ModerationActions moderationAction) {
        return moderationActionsRepository.save(moderationAction);
    }

    public ModerationActions getModerationAction(Long id) {
        return moderationActionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Moderation action not found"));
    }

    public List<ModerationActions> getAllModerationActions() {
        return moderationActionsRepository.findAll();
    }

    public ModerationActions updateModerationAction(Long id, ModerationActions updated) {
        ModerationActions existing = getModerationAction(id);

        existing.setActionType(updated.getActionType());
        existing.setContentId(updated.getContentId());
        existing.setModeratorUsername(updated.getModeratorUsername());
        existing.setReason(updated.getReason());
        existing.setTimestamp(updated.getTimestamp());

        return moderationActionsRepository.save(existing);
    }

    public void deleteModerationAction(Long id) {
        moderationActionsRepository.deleteById(id);
    }
}
