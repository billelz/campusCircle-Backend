package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.DirectMessage;
import com.example.campusCircle.service.DirectMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/direct-messages")
@CrossOrigin(origins = "*")
public class DirectMessageController {

    @Autowired
    private DirectMessageService directMessageService;

    @GetMapping
    public ResponseEntity<List<DirectMessage>> getAllConversations() {
        return ResponseEntity.ok(directMessageService.getAllConversations());
    }

    // Get current user's conversations
    @GetMapping("/my")
    public ResponseEntity<List<DirectMessage>> getMyConversations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        return ResponseEntity.ok(directMessageService.getUserConversations(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectMessage> getConversationById(@PathVariable String id) {
        return directMessageService.getConversationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<DirectMessage> getConversationByConversationId(@PathVariable String conversationId) {
        return directMessageService.getConversationByConversationId(conversationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<DirectMessage>> getUserConversations(@PathVariable String username) {
        return ResponseEntity.ok(directMessageService.getUserConversations(username));
    }

    @GetMapping("/between")
    public ResponseEntity<DirectMessage> getConversationBetweenUsers(
            @RequestParam String user1,
            @RequestParam String user2) {
        return directMessageService.getConversationBetweenUsers(user1, user2)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("unreadCount", directMessageService.getUnreadCount(username));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start")
    public ResponseEntity<DirectMessage> startConversation(@RequestBody StartConversationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String currentUser = auth.getName();
        
        // Support both formats: recipientUsername (from mobile) or user1/user2 (legacy)
        String recipient = request.getRecipientUsername() != null ? request.getRecipientUsername() : request.getUser2();
        if (recipient == null) {
            return ResponseEntity.badRequest().build();
        }
        
        DirectMessage dm = directMessageService.getOrCreateConversation(currentUser, recipient);
        return ResponseEntity.status(HttpStatus.CREATED).body(dm);
    }

    @PostMapping("/send")
    public ResponseEntity<DirectMessage> sendMessage(@RequestBody SendMessageRequest request) {
        DirectMessage dm = directMessageService.sendMessageToUser(
                request.getSenderUsername(),
                request.getRecipientUsername(),
                request.getText(),
                request.getMediaUrls()
        );
        if (dm != null) {
            return ResponseEntity.ok(dm);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/conversation/{conversationId}/send")
    public ResponseEntity<DirectMessage> sendMessageToConversation(
            @PathVariable String conversationId,
            @RequestBody SendToConversationRequest request) {
        DirectMessage dm = directMessageService.sendMessage(
                conversationId,
                request.getSender(),
                request.getText(),
                request.getMediaUrls()
        );
        if (dm != null) {
            return ResponseEntity.ok(dm);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/conversation/{conversationId}/read")
    public ResponseEntity<DirectMessage> markAsRead(
            @PathVariable String conversationId,
            @RequestParam(required = false) String reader) {
        // Use authenticated user if reader not provided
        String readerUsername = reader;
        if (readerUsername == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                readerUsername = auth.getName();
            }
        }
        if (readerUsername == null) {
            return ResponseEntity.badRequest().build();
        }
        DirectMessage dm = directMessageService.markMessagesAsRead(conversationId, readerUsername);
        if (dm != null) {
            return ResponseEntity.ok(dm);
        }
        return ResponseEntity.notFound().build();
    }

    // Alternative endpoint for mobile app: POST /direct-messages/{id}/read
    @PostMapping("/{id}/read")
    public ResponseEntity<DirectMessage> markConversationAsRead(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String reader = auth.getName();
        Optional<DirectMessage> conversationOpt = directMessageService.getConversationById(id);
        if (conversationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DirectMessage updated = directMessageService.markMessagesAsRead(
                conversationOpt.get().getConversationId(),
                reader
        );

        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // Alternative endpoint for mobile app: POST /direct-messages/{id}/messages
    @PostMapping("/{id}/messages")
    public ResponseEntity<DirectMessage> sendMessageToId(
            @PathVariable String id,
            @RequestBody SendToConversationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String sender = auth.getName();
        Optional<DirectMessage> conversationOpt = directMessageService.getConversationById(id);
        if (conversationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DirectMessage updated = directMessageService.sendMessage(
                conversationOpt.get().getConversationId(),
                sender,
                request.getText(),
                request.getMediaUrls()
        );

        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String conversationId) {
        directMessageService.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }

    // DTOs
    public static class StartConversationRequest {
        private String user1;
        private String user2;
        private String recipientUsername;

        public String getUser1() { return user1; }
        public void setUser1(String user1) { this.user1 = user1; }
        public String getUser2() { return user2; }
        public void setUser2(String user2) { this.user2 = user2; }
        public String getRecipientUsername() { return recipientUsername; }
        public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }
    }

    public static class SendMessageRequest {
        private String senderUsername;
        private String recipientUsername;
        private String text;
        private List<String> mediaUrls;

        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
        public String getRecipientUsername() { return recipientUsername; }
        public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public List<String> getMediaUrls() { return mediaUrls; }
        public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }
    }

    public static class SendToConversationRequest {
        private String sender;
        private String text;
        private List<String> mediaUrls;

        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public List<String> getMediaUrls() { return mediaUrls; }
        public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }
    }
}
