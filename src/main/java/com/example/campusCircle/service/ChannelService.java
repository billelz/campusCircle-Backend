package com.example.campusCircle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Channel;
import com.example.campusCircle.repository.ChannelRepository;

@Service
public class ChannelService {

    private final ChannelRepository ChannelRepository;

    public ChannelService(ChannelRepository ChannelRepository) {
        this.ChannelRepository = ChannelRepository;
    }

    public Channel createChannel(Channel Channel) {
        return ChannelRepository.save(Channel);
    }

    public Channel getChannel(Long id) {
        return ChannelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
    }

    public List<Channel> getAllChannels() {
        return ChannelRepository.findAll();
    }

    public Channel updateChannel(Long id, Channel updated) {
        Channel existing = getChannel(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setRules(updated.getRules());
        existing.setUniversity(updated.getUniversity());
        existing.setCreatedBy(updated.getCreatedBy());
        existing.setSubscriberCount(updated.getSubscriberCount());


        return ChannelRepository.save(existing);
    }

    public void deleteChannel(Long id) {
        ChannelRepository.deleteById(id);
    }
}
