package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.DirectMessage;
import com.example.campusCircle.service.DirectMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        DirectMessage dm = directMessageService.getOrCreateConversation(request.getUser1(), request.getUser2());
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
            @RequestParam String reader) {
        DirectMessage dm = directMessageService.markMessagesAsRead(conversationId, reader);
        if (dm != null) {
            return ResponseEntity.ok(dm);
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

        public String getUser1() { return user1; }
        public void setUser1(String user1) { this.user1 = user1; }
        public String getUser2() { return user2; }
        public void setUser2(String user2) { this.user2 = user2; }
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
