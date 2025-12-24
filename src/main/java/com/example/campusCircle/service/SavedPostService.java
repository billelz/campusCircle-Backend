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

    public Optional<SavedPost> getSavedPostsByUserId(String userId) {
        return savedPostRepository.findByUserId(userId);
    }

    public List<SavedPost> getUsersWhoSavedPost(String postId) {
        return savedPostRepository.findByPostIdsContaining(postId);
    }

    public SavedPost savePost(SavedPost savedPost) {
        if (savedPost.getSavedAt() == null) {
            savedPost.setSavedAt(LocalDateTime.now());
        }
        return savedPostRepository.save(savedPost);
    }

    public SavedPost addPostToUserSaved(String userId, String postId) {
        Optional<SavedPost> existingSavedPost = savedPostRepository.findByUserId(userId);

        if (existingSavedPost.isPresent()) {
            SavedPost savedPost = existingSavedPost.get();
            if (!savedPost.getPostIds().contains(postId)) {
                savedPost.getPostIds().add(postId);
            }
            savedPost.setSavedAt(LocalDateTime.now());
            return savedPostRepository.save(savedPost);
        } else {
            SavedPost newSavedPost = new SavedPost();
            newSavedPost.setUserId(userId);
            List<String> postIds = new ArrayList<>();
            postIds.add(postId);
            newSavedPost.setPostIds(postIds);
            newSavedPost.setSavedAt(LocalDateTime.now());
            return savedPostRepository.save(newSavedPost);
        }
    }

    public SavedPost removePostFromUserSaved(String userId, String postId) {
        Optional<SavedPost> existingSavedPost = savedPostRepository.findByUserId(userId);

        if (existingSavedPost.isPresent()) {
            SavedPost savedPost = existingSavedPost.get();
            savedPost.getPostIds().remove(postId);
            if (savedPost.getPostIds().isEmpty()) {
                savedPostRepository.delete(savedPost);
                return null;
            }
            savedPost.setSavedAt(LocalDateTime.now());
            return savedPostRepository.save(savedPost);
        }
        return null;
    }

    public boolean isPostSavedByUser(String userId, String postId) {
        Optional<SavedPost> savedPost = savedPostRepository.findByUserId(userId);
        return savedPost.isPresent() && savedPost.get().getPostIds().contains(postId);
    }

    public void deleteSavedPost(String id) {
        savedPostRepository.deleteById(id);
    }

    public void deleteSavedPostsByUserId(String userId) {
        savedPostRepository.deleteByUserId(userId);
    }
}
