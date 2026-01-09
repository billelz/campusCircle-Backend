package com.example.campusCircle.service;

import com.example.campusCircle.model.Subscription;
import com.example.campusCircle.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ChannelService channelService;

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    public List<Subscription> getChannelSubscribers(Long channelId) {
        return subscriptionRepository.findByChannelId(channelId);
    }

    public Long getSubscriberCount(Long channelId) {
        return subscriptionRepository.countByChannelId(channelId);
    }

    public boolean isSubscribed(Long userId, Long channelId) {
        return subscriptionRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    @Transactional
    public Subscription subscribe(Long userId, Long channelId) {
        // Check if already subscribed
        if (subscriptionRepository.existsByUserIdAndChannelId(userId, channelId)) {
            return subscriptionRepository.findByUserIdAndChannelId(userId, channelId).orElse(null);
        }

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setChannelId(channelId);
        subscription.setNotificationEnabled(true);
        Subscription saved = subscriptionRepository.save(subscription);

        // Update channel subscriber count
        channelService.incrementSubscriberCount(channelId);

        return saved;
    }

    @Transactional
    public void unsubscribe(Long userId, Long channelId) {
        if (subscriptionRepository.existsByUserIdAndChannelId(userId, channelId)) {
            subscriptionRepository.deleteByUserIdAndChannelId(userId, channelId);
            channelService.decrementSubscriberCount(channelId);
        }
    }

    @Transactional
    public Subscription toggleNotifications(Long userId, Long channelId, boolean enabled) {
        return subscriptionRepository.findByUserIdAndChannelId(userId, channelId)
                .map(subscription -> {
                    subscription.setNotificationEnabled(enabled);
                    return subscriptionRepository.save(subscription);
                })
                .orElse(null);
    }

    public List<Subscription> getSubscriptionsWithNotifications(Long userId) {
        return subscriptionRepository.findByUserIdAndNotificationEnabledTrue(userId);
    }
}
