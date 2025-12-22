package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.CommentContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentContentRepository extends MongoRepository<CommentContent, String> {

    Optional<CommentContent> findByCommentId(String commentId);

    List<CommentContent> findByBodyTextContaining(String keyword);

    void deleteByCommentId(String commentId);
}