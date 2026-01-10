package com.example.campusCircle.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Channel.ChannelCategory;
import com.example.campusCircle.repository.ChannelRepository;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel createChannel(Channel channel) {
        if (channelRepository.existsByUniversityIdAndName(channel.getUniversityId(), channel.getName())) {
            throw new RuntimeException("Channel with this name already exists in this university");
        }
        channel.setCreatedAt(LocalDateTime.now());
        channel.setIsActive(true);
        channel.setSubscriberCount(0);
        return channelRepository.save(channel);
    }

    public Channel getChannel(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
    }

    public Channel getChannelByName(String name) {
        return channelRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
    }

    public List<Channel> getAllChannels() {
        return channelRepository.findActiveChannelsOrderBySubscribers();
    }

    public Page<Channel> getAllChannels(Pageable pageable) {
        return channelRepository.findActiveChannels(pageable);
    }

    public List<Channel> getChannelsByUniversity(Long universityId) {
        return channelRepository.findActiveByUniversity(universityId);
    }

    public List<Channel> getChannelsByCategory(ChannelCategory category) {
        return channelRepository.findByCategory(category);
    }

    public List<Channel> searchChannels(String query) {
        return channelRepository.searchChannels(query);
    }

    public Channel updateChannel(Long id, Channel updated) {
        Channel existing = getChannel(id);

        if (updated.getName() != null) {
            existing.setName(updated.getName());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        if (updated.getRules() != null) {
            existing.setRules(updated.getRules());
        }
        if (updated.getCategory() != null) {
            existing.setCategory(updated.getCategory());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        return channelRepository.save(existing);
    }

    public void deleteChannel(Long id) {
        Channel channel = getChannel(id);
        channel.setIsActive(false);
        channel.setUpdatedAt(LocalDateTime.now());
        channelRepository.save(channel);
    }

    public void incrementSubscriberCount(Long channelId) {
        Channel channel = getChannel(channelId);
        channel.setSubscriberCount(channel.getSubscriberCount() + 1);
        channelRepository.save(channel);
    }

    public void decrementSubscriberCount(Long channelId) {
        Channel channel = getChannel(channelId);
        if (channel.getSubscriberCount() > 0) {
            channel.setSubscriberCount(channel.getSubscriberCount() - 1);
            channelRepository.save(channel);
        }
    }
}
