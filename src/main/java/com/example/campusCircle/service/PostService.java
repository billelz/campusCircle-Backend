package com.example.campusCircle.service;

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
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<Post> getAllActivePosts() {
        return postRepository.findAllActive();
    }

    public Post updatePost(Long id, Post updated) {
        Post existing = getPostById(id);

        existing.setTitle(updated.getTitle());
        existing.setAuthorUsername(updated.getAuthorUsername());
        existing.setChannelId(updated.getChannelId());

        return postRepository.save(existing);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}

