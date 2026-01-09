package com.example.campusCircle.controller;

import com.example.campusCircle.model.Vote;
import com.example.campusCircle.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> vote(@RequestBody VoteRequest request) {
        Vote vote = voteService.vote(
                request.getUserId(),
                request.getContentId(),
                request.getContentType(),
                request.getVoteValue(),
                request.getAuthorUserId(),
                request.getChannelId()
        );

        Map<String, Object> response = new HashMap<>();
        if (vote != null) {
            response.put("status", "voted");
            response.put("vote", vote);
        } else {
            response.put("status", "removed");
            response.put("message", "Vote removed");
        }
        response.put("newVoteCount", voteService.getVoteCount(request.getContentId(), request.getContentType()));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/content/{contentId}/type/{contentType}")
    public ResponseEntity<Vote> getUserVote(
            @PathVariable Long userId,
            @PathVariable Long contentId,
            @PathVariable Vote.ContentType contentType) {
        return voteService.getUserVote(userId, contentId, contentType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count/{contentId}/type/{contentType}")
    public ResponseEntity<Map<String, Object>> getVoteCount(
            @PathVariable Long contentId,
            @PathVariable Vote.ContentType contentType) {
        Map<String, Object> response = new HashMap<>();
        response.put("contentId", contentId);
        response.put("contentType", contentType);
        response.put("voteCount", voteService.getVoteCount(contentId, contentType));
        response.put("upvotes", voteService.getUpvoteCount(contentId, contentType));
        response.put("downvotes", voteService.getDownvoteCount(contentId, contentType));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}/content/{contentId}/type/{contentType}")
    public ResponseEntity<Void> removeVote(
            @PathVariable Long userId,
            @PathVariable Long contentId,
            @PathVariable Vote.ContentType contentType) {
        voteService.removeVote(userId, contentId, contentType);
        return ResponseEntity.noContent().build();
    }

    // DTO for vote request
    public static class VoteRequest {
        private Long userId;
        private Long contentId;
        private Vote.ContentType contentType;
        private int voteValue;
        private Long authorUserId;
        private Long channelId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getContentId() { return contentId; }
        public void setContentId(Long contentId) { this.contentId = contentId; }
        public Vote.ContentType getContentType() { return contentType; }
        public void setContentType(Vote.ContentType contentType) { this.contentType = contentType; }
        public int getVoteValue() { return voteValue; }
        public void setVoteValue(int voteValue) { this.voteValue = voteValue; }
        public Long getAuthorUserId() { return authorUserId; }
        public void setAuthorUserId(Long authorUserId) { this.authorUserId = authorUserId; }
        public Long getChannelId() { return channelId; }
        public void setChannelId(Long channelId) { this.channelId = channelId; }
    }
}
