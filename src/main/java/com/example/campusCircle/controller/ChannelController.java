package com.example.campusCircle.controller;


import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.Channel;
import com.example.campusCircle.service.ChannelService;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService ChannelService;

    public ChannelController(ChannelService ChannelService) {
        this.ChannelService = ChannelService;
    }

    @PostMapping
    public Channel create(@RequestBody Channel Channel) {
        return ChannelService.createChannel(Channel);
    }

    @GetMapping("/{id}")
    public Channel getOne(@PathVariable Long id) {
        return ChannelService.getChannel(id);
    }

    @GetMapping
    public List<Channel> getAll() {
        return ChannelService.getAllChannels();
    }

    @PutMapping("/{id}")
    public Channel update(@PathVariable Long id, @RequestBody Channel updated) {
        return ChannelService.updateChannel(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ChannelService.deleteChannel(id);
    }
}
