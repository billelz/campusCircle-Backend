package com.example.campusCircle.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Post;
import com.example.campusCircle.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<Post> getAllActivePosts() {
        return postRepository.findAllActive();
    }

    public List<Post> getPostsByChannel(Long channelId) {
        return postRepository.findByChannelIdAndDeletedAtIsNullOrderByCreatedAtDesc(channelId);
    }

    public List<Post> getPostsByAuthor(String username) {
        return postRepository.findByAuthorUsernameAndDeletedAtIsNullOrderByCreatedAtDesc(username);
    }

    public List<Post> getTrendingPosts(int limit) {
        return postRepository.findTrendingPosts(limit);
    }

    public Post updatePost(Long id, Post updated) {
        Post existing = getPostById(id);

        existing.setTitle(updated.getTitle());
        existing.setEditedAt(LocalDateTime.now());

        return postRepository.save(existing);
    }

    public void deletePost(Long id) {
        Post post = getPostById(id);
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public void incrementCommentCount(Long postId) {
        Post post = getPostById(postId);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
    }

    public void decrementCommentCount(Long postId) {
        Post post = getPostById(postId);
        if (post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
        }
        postRepository.save(post);
    }

    public void updateVoteCounts(Long postId, int upvoteChange, int downvoteChange) {
        Post post = getPostById(postId);
        post.setUpvoteCount(post.getUpvoteCount() + upvoteChange);
        post.setDownvoteCount(post.getDownvoteCount() + downvoteChange);
        postRepository.save(post);
    }

    public List<Post> searchPosts(String query) {
        return postRepository.searchPosts(query);
    }

    public List<Object[]> getTopContributorsByUpvotes() {
        return postRepository.findTopContributorsByUpvotes();
    }
}

