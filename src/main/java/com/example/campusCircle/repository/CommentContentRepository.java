package com.example.campusCircle.repository;

import com.example.campusCircle.model.CommentContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentContentRepository extends MongoRepository<CommentContent, String> {

    Optional<CommentContent> findByCommentId(Long commentId);

    List<CommentContent> findByContentContaining(String keyword);

    void deleteByCommentId(Long commentId);

    // Alias for search functionality
    default List<CommentContent> findByBodyTextContaining(String keyword) {
        return findByContentContaining(keyword);
    }
}