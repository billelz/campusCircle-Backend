package com.example.campusCircle.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    // Track online users: username -> sessionId
    private static final Map<String, String> onlineUsers = new ConcurrentHashMap<>();
    private static final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();

        if (user != null && sessionId != null) {
            String username = user.getName();
            onlineUsers.put(username, sessionId);
            sessionToUser.put(sessionId, username);

            // Broadcast online status to all subscribers
            WebSocketMessage statusMessage = WebSocketMessage.onlineStatus(username, true);
            messagingTemplate.convertAndSend("/topic/online-status", statusMessage);

            System.out.println("User connected: " + username + " (Session: " + sessionId + ")");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionId != null) {
            String username = sessionToUser.remove(sessionId);
            
            if (username != null) {
                onlineUsers.remove(username);

                // Broadcast offline status to all subscribers
                WebSocketMessage statusMessage = WebSocketMessage.onlineStatus(username, false);
                messagingTemplate.convertAndSend("/topic/online-status", statusMessage);

                System.out.println("User disconnected: " + username);
            }
        }
    }

    public static boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    public static Set<String> getOnlineUsers() {
        return onlineUsers.keySet();
    }

    public static int getOnlineUserCount() {
        return onlineUsers.size();
    }
}
