package com.example.campusCircle.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.campusCircle.dto.ChannelRequest;
import com.example.campusCircle.dto.ChannelResponse;
import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Channel.ChannelCategory;
import com.example.campusCircle.service.ChannelService;
import com.example.campusCircle.service.ChannelSubscriptionService;
import com.example.campusCircle.service.UniversityService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final ChannelSubscriptionService subscriptionService;
    private final UniversityService universityService;

    public ChannelController(ChannelService channelService, 
                            ChannelSubscriptionService subscriptionService,
                            UniversityService universityService) {
        this.channelService = channelService;
        this.subscriptionService = subscriptionService;
        this.universityService = universityService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ChannelRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            Channel channel = new Channel();
            channel.setName(request.getName());
            channel.setDescription(request.getDescription());
            channel.setRules(request.getRules());
            channel.setUniversityId(request.getUniversityId());
            channel.setCategory(request.getCategory());
            channel.setCreatedBy(auth.getName());

            Channel created = channelService.createChannel(channel);
            
            // Auto-subscribe the creator to their channel
            try {
                subscriptionService.subscribe(auth.getName(), created.getId());
            } catch (Exception e) {
                // Log but don't fail if subscription fails
                System.out.println("Warning: Could not auto-subscribe creator to channel: " + e.getMessage());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created, auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Channel channel = channelService.getChannel(id);
            String currentUser = getCurrentUsername();
            return ResponseEntity.ok(mapToResponse(channel, currentUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Channel> channels = channelService.getAllChannels(pageable);
        String currentUser = getCurrentUsername();
        
        List<ChannelResponse> responses = channels.getContent().stream()
                .map(channel -> mapToResponse(channel, currentUser))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<ChannelResponse>> getByUniversity(@PathVariable Long universityId) {
        List<Channel> channels = channelService.getChannelsByUniversity(universityId);
        String currentUser = getCurrentUsername();
        
        List<ChannelResponse> responses = channels.stream()
                .map(channel -> mapToResponse(channel, currentUser))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ChannelResponse>> getByCategory(@PathVariable String category) {
        try {
            ChannelCategory channelCategory = ChannelCategory.valueOf(category.toUpperCase());
            List<Channel> channels = channelService.getChannelsByCategory(channelCategory);
            String currentUser = getCurrentUsername();
            
            List<ChannelResponse> responses = channels.stream()
                    .map(channel -> mapToResponse(channel, currentUser))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChannelResponse>> search(@RequestParam String q) {
        List<Channel> channels = channelService.searchChannels(q);
        String currentUser = getCurrentUsername();
        
        List<ChannelResponse> responses = channels.stream()
                .map(channel -> mapToResponse(channel, currentUser))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ChannelRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            Channel existing = channelService.getChannel(id);
            if (!existing.getCreatedBy().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only channel creator can update");
            }

            Channel updated = new Channel();
            updated.setName(request.getName());
            updated.setDescription(request.getDescription());
            updated.setRules(request.getRules());
            updated.setCategory(request.getCategory());

            Channel result = channelService.updateChannel(id, updated);
            return ResponseEntity.ok(mapToResponse(result, auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            Channel existing = channelService.getChannel(id);
            if (!existing.getCreatedBy().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only channel creator can delete");
            }

            channelService.deleteChannel(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<?> subscribe(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            subscriptionService.subscribe(auth.getName(), id);
            channelService.incrementSubscriberCount(id);
            
            Channel channel = channelService.getChannel(id);
            return ResponseEntity.ok(mapToResponse(channel, auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/subscribe")
    public ResponseEntity<?> unsubscribe(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            subscriptionService.unsubscribe(auth.getName(), id);
            channelService.decrementSubscriberCount(id);
            
            Channel channel = channelService.getChannel(id);
            return ResponseEntity.ok(mapToResponse(channel, auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<ChannelCategory[]> getCategories() {
        return ResponseEntity.ok(ChannelCategory.values());
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<?> getMySubscriptions() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            List<Channel> subscribedChannels = subscriptionService.getSubscribedChannels(auth.getName());
            
            List<ChannelResponse> responses = subscribedChannels.stream()
                    .map(channel -> mapToResponse(channel, auth.getName()))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }

    private ChannelResponse mapToResponse(Channel channel, String currentUser) {
        boolean isSubscribed = false;
        if (currentUser != null) {
            isSubscribed = subscriptionService.isSubscribed(currentUser, channel.getId());
        }

        String universityName = null;
        if (channel.getUniversityId() != null) {
            try {
                universityName = universityService.getUniversity(channel.getUniversityId()).getName();
            } catch (Exception e) {
                // University not found, leave as null
            }
        }

        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .rules(channel.getRules())
                .universityId(channel.getUniversityId())
                .universityName(universityName)
                .category(channel.getCategory() != null ? channel.getCategory().name() : null)
                .createdByUsername(channel.getCreatedBy())
                .subscriberCount(channel.getSubscriberCount())
                .isSubscribed(isSubscribed)
                .createdAt(channel.getCreatedAt())
                .build();
    }
}
