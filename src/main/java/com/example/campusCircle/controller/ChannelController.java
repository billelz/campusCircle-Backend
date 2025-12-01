package com.example.campuscircle.controller;

import org.springframework.web.bind.annotation.*;
import com.example.campuscircle.model.Channel;
import com.example.campuscircle.service.ChannelService;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // Créer un nouveau channel
    @PostMapping
    public Channel create(@RequestBody Channel channel) {
        return channelService.createChannel(channel);
    }

    // Récupérer un channel par ID
    @GetMapping("/{id}")
    public Channel getOne(@PathVariable Long id) {
        return channelService.getChannel(id);
    }

    // Récupérer tous les channels
    @GetMapping
    public List<Channel> getAll() {
        return channelService.getAllChannels();
    }

    // Récupérer les channels par université
    @GetMapping("/university/{universityId}")
    public List<Channel> getByUniversity(@PathVariable Long universityId) {
        return channelService.getChannelsByUniversity(universityId);
    }

    // Mettre à jour un channel
    @PutMapping("/{id}")
    public Channel update(@PathVariable Long id, @RequestBody Channel updated) {
        return channelService.updateChannel(id, updated);
    }

    // Supprimer un channel
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        channelService.deleteChannel(id);
    }

    // S'abonner à un channel
    @PostMapping("/{id}/subscribe")
    public void subscribe(@PathVariable Long id, @RequestParam Long userId) {
        channelService.subscribeToChannel(id, userId);
    }

    // Se désabonner d'un channel
    @DeleteMapping("/{id}/unsubscribe")
    public void unsubscribe(@PathVariable Long id, @RequestParam Long userId) {
        channelService.unsubscribeFromChannel(id, userId);
    }
}