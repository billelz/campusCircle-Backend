package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.DirectMessage;
import com.example.campusCircle.repository.DirectMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DirectMessageService {

    @Autowired
    private DirectMessageRepository directMessageRepository;

    public List<DirectMessage> getAllConversations() {
        return directMessageRepository.findAll();
    }

    public Optional<DirectMessage> getConversationById(String id) {
        return directMessageRepository.findById(id);
    }

    public Optional<DirectMessage> getConversationByConversationId(String conversationId) {
        return directMessageRepository.findByConversationId(conversationId);
    }

    public List<DirectMessage> getUserConversations(String username) {
        return directMessageRepository.findConversationsByUserSorted(username);
    }

    public Optional<DirectMessage> getConversationBetweenUsers(String user1, String user2) {
        return directMessageRepository.findConversationBetweenUsers(user1, user2);
    }

    public DirectMessage getOrCreateConversation(String user1, String user2) {
        return directMessageRepository.findConversationBetweenUsers(user1, user2)
                .orElseGet(() -> {
                    DirectMessage dm = new DirectMessage();
                    dm.setConversationId(UUID.randomUUID().toString());
                    dm.setParticipants(List.of(user1, user2));
                    dm.setCreatedAt(LocalDateTime.now());
                    dm.setUpdatedAt(LocalDateTime.now());
                    return directMessageRepository.save(dm);
                });
    }

    public DirectMessage sendMessage(String conversationId, String sender, String text, List<String> mediaUrls) {
        return directMessageRepository.findByConversationId(conversationId)
                .map(dm -> {
                    DirectMessage.Message message = new DirectMessage.Message();
                    message.setMessageId(UUID.randomUUID().toString());
                    message.setSender(sender);
                    message.setText(text);
                    message.setMediaUrls(mediaUrls);
                    message.setTimestamp(LocalDateTime.now());
                    message.setRead(false);
                    
                    dm.addMessage(message);
                    return directMessageRepository.save(dm);
                })
                .orElse(null);
    }

    public DirectMessage sendMessageToUser(String senderUsername, String recipientUsername, String text, List<String> mediaUrls) {
        DirectMessage dm = getOrCreateConversation(senderUsername, recipientUsername);
        return sendMessage(dm.getConversationId(), senderUsername, text, mediaUrls);
    }

    public DirectMessage markMessagesAsRead(String conversationId, String reader) {
        return directMessageRepository.findByConversationId(conversationId)
                .map(dm -> {
                    boolean updated = false;
                    for (DirectMessage.Message msg : dm.getMessages()) {
                        if (!msg.getSender().equals(reader) && !msg.isRead()) {
                            msg.setRead(true);
                            msg.setReadAt(LocalDateTime.now());
                            updated = true;
                        }
                    }
                    if (updated) {
                        return directMessageRepository.save(dm);
                    }
                    return dm;
                })
                .orElse(null);
    }

    public int getUnreadCount(String username) {
        return directMessageRepository.findByParticipant(username).stream()
                .flatMap(dm -> dm.getMessages().stream())
                .filter(msg -> !msg.getSender().equals(username) && !msg.isRead())
                .mapToInt(msg -> 1)
                .sum();
    }

    public void deleteConversation(String conversationId) {
        directMessageRepository.findByConversationId(conversationId)
                .ifPresent(directMessageRepository::delete);
    }
}
