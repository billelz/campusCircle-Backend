package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.SavedPost;
import com.example.campusCircle.repository.SavedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SavedPostService {

    @Autowired
    private SavedPostRepository savedPostRepository;

    public List<SavedPost> getAllSavedPosts() {
        return savedPostRepository.findAll();
    }

    public Optional<SavedPost> getSavedPostById(String id) {
        return savedPostRepository.findById(id);
    }

    public Optional<SavedPost> getSavedPostsByUserId(Long userId) {
        return savedPostRepository.findByUserId(userId);
    }

    public Optional<SavedPost> getSavedPostsByUsername(String username) {
        return savedPostRepository.findByUsername(username);
    }

    public SavedPost getOrCreateUserSavedPosts(Long userId, String username) {
        return savedPostRepository.findByUserId(userId)
                .orElseGet(() -> {
                    SavedPost savedPost = new SavedPost();
                    savedPost.setUserId(userId);
                    savedPost.setUsername(username);
                    savedPost.setSavedItems(new ArrayList<>());
                    savedPost.setCreatedAt(LocalDateTime.now());
                    savedPost.setUpdatedAt(LocalDateTime.now());
                    return savedPostRepository.save(savedPost);
                });
    }

    public SavedPost savePost(Long userId, String username, Long postId, String postTitle, Long channelId, String channelName, String folder) {
        SavedPost savedPost = getOrCreateUserSavedPosts(userId, username);
        
        SavedPost.SavedItem item = new SavedPost.SavedItem();
        item.setPostId(postId);
        item.setPostTitle(postTitle);
        item.setChannelId(channelId);
        item.setChannelName(channelName);
        item.setSavedAt(LocalDateTime.now());
        item.setFolder(folder);
        
        savedPost.addSavedItem(item);
        return savedPostRepository.save(savedPost);
    }

    public SavedPost unsavePost(Long userId, Long postId) {
        return savedPostRepository.findByUserId(userId)
                .map(savedPost -> {
                    savedPost.removeSavedItem(postId);
                    return savedPostRepository.save(savedPost);
                })
                .orElse(null);
    }

    public boolean isPostSaved(Long userId, Long postId) {
        return savedPostRepository.findByUserId(userId)
                .map(savedPost -> savedPost.getSavedItems().stream()
                        .anyMatch(item -> item.getPostId().equals(postId)))
                .orElse(false);
    }

    public List<SavedPost.SavedItem> getSavedItemsByFolder(Long userId, String folder) {
        return savedPostRepository.findByUserId(userId)
                .map(savedPost -> savedPost.getSavedItems().stream()
                        .filter(item -> folder.equals(item.getFolder()))
                        .toList())
                .orElse(new ArrayList<>());
    }

    public void deleteSavedPosts(Long userId) {
        savedPostRepository.deleteByUserId(userId);
    }
}
