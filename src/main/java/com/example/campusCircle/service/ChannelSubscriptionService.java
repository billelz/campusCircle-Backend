package com.example.campusCircle.service;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Subscription;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.repository.ChannelRepository;
import com.example.campusCircle.repository.UsersRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service wrapper for channel subscriptions that works with usernames
 * instead of user IDs for easier integration with authentication.
 */
@Service
public class ChannelSubscriptionService {

    private final SubscriptionService subscriptionService;
    private final UsersRepository usersRepository;
    private final ChannelRepository channelRepository;

    public ChannelSubscriptionService(SubscriptionService subscriptionService, 
                                      UsersRepository usersRepository,
                                      ChannelRepository channelRepository) {
        this.subscriptionService = subscriptionService;
        this.usersRepository = usersRepository;
        this.channelRepository = channelRepository;
    }

    public void subscribe(String username, Long channelId) {
        Long userId = getUserId(username);
        subscriptionService.subscribe(userId, channelId);
    }

    public void unsubscribe(String username, Long channelId) {
        Long userId = getUserId(username);
        subscriptionService.unsubscribe(userId, channelId);
    }

    public boolean isSubscribed(String username, Long channelId) {
        Long userId = getUserId(username);
        return subscriptionService.isSubscribed(userId, channelId);
    }

    public Long getSubscriberCount(Long channelId) {
        return subscriptionService.getSubscriberCount(channelId);
    }

    public List<Channel> getSubscribedChannels(String username) {
        Long userId = getUserId(username);
        List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(userId);
        
        return subscriptions.stream()
                .map(sub -> channelRepository.findById(sub.getChannelId()).orElse(null))
                .filter(channel -> channel != null)
                .collect(Collectors.toList());
    }

    private Long getUserId(String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getId();
    }
}
