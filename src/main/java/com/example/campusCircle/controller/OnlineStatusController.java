package com.example.campusCircle.controller;

import com.example.campusCircle.websocket.WebSocketEventListener;
import com.example.campusCircle.websocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/online-status")
@CrossOrigin(origins = "*")
public class OnlineStatusController {

    @Autowired
    private WebSocketService webSocketService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOnlineStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("onlineCount", webSocketService.getOnlineUserCount());
        response.put("onlineUsers", webSocketService.getOnlineUsers());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getOnlineCount() {
        Map<String, Integer> response = new HashMap<>();
        response.put("count", webSocketService.getOnlineUserCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<Set<String>> getOnlineUsers() {
        return ResponseEntity.ok(webSocketService.getOnlineUsers());
    }

    @GetMapping("/check/{username}")
    public ResponseEntity<Map<String, Object>> checkUserOnline(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("online", webSocketService.isUserOnline(username));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkMultipleUsers(@RequestBody Set<String> usernames) {
        Map<String, Boolean> response = new HashMap<>();
        for (String username : usernames) {
            response.put(username, webSocketService.isUserOnline(username));
        }
        return ResponseEntity.ok(response);
    }
}
