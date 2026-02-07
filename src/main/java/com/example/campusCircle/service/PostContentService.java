package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.PostContent;
import com.example.campusCircle.repository.PostContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostContentService {

    @Autowired
    private PostContentRepository postContentRepository;

    public List<PostContent> getAllPosts() {
        return postContentRepository.findAll();
    }

    public Optional<PostContent> getPostById(String id) {
        return postContentRepository.findById(id);
    }

    public PostContent getPostContent(Long postId) {
        return postContentRepository.findByPostId(String.valueOf(postId))
                .orElseThrow(() -> new RuntimeException("Post content not found"));
    }

    public Optional<PostContent> getPostByPostId(String postId) {
        return postContentRepository.findByPostId(postId);
    }

    public List<PostContent> getPopularPosts(Long minViews) {
        return postContentRepository.findByViewCountGreaterThan(minViews);
    }

    public List<PostContent> getTrendingPosts(Double minScore) {
        return postContentRepository.findByTrendingScoreGreaterThanOrderByTrendingScoreDesc(minScore);
    }

    public List<PostContent> searchPosts(String keyword) {
        return postContentRepository.findByBodyTextContaining(keyword);
    }

    public PostContent createPostContent(PostContent postContent) {
        if (postContent.getViewCount() == null) {
            postContent.setViewCount(0L);
        }
        if (postContent.getTrendingScore() == null) {
            postContent.setTrendingScore(0.0);
        }
        return postContentRepository.save(postContent);
    }

    public PostContent savePost(PostContent postContent) {
        if (postContent.getViewCount() == null) {
            postContent.setViewCount(0L);
        }
        if (postContent.getTrendingScore() == null) {
            postContent.setTrendingScore(0.0);
        }
        return postContentRepository.save(postContent);
    }

    public PostContent updatePostContent(Long postId, String content, List<String> mediaUrls) {
        Optional<PostContent> existing = postContentRepository.findByPostId(String.valueOf(postId));
        if (existing.isPresent()) {
            PostContent postContent = existing.get();
            postContent.setBodyText(content);
            postContent.setMediaUrls(mediaUrls);
            return postContentRepository.save(postContent);
        }
        throw new RuntimeException("Post content not found");
    }

    public PostContent incrementViewCount(Long postId) {
        Optional<PostContent> post = postContentRepository.findByPostId(String.valueOf(postId));
        if (post.isPresent()) {
            PostContent postContent = post.get();
            postContent.setViewCount(postContent.getViewCount() + 1);
            return postContentRepository.save(postContent);
        }
        return null;
    }

    public PostContent incrementViewCount(String postId) {
        Optional<PostContent> post = postContentRepository.findByPostId(postId);
        if (post.isPresent()) {
            PostContent postContent = post.get();
            postContent.setViewCount(postContent.getViewCount() + 1);
            return postContentRepository.save(postContent);
        }
        return null;
    }

    public PostContent updateTrendingScore(String postId, Double newScore) {
        Optional<PostContent> post = postContentRepository.findByPostId(postId);
        if (post.isPresent()) {
            PostContent postContent = post.get();
            postContent.setTrendingScore(newScore);
            return postContentRepository.save(postContent);
        }
        return null;
    }

    public void deletePost(String id) {
        postContentRepository.deleteById(id);
    }

    public void deletePostByPostId(String postId) {
        postContentRepository.deleteByPostId(postId);
    }
}