package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.SavedPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPostRepository extends MongoRepository<SavedPost, String> {

    Optional<SavedPost> findByUserId(String userId);

    List<SavedPost> findByPostIdsContaining(String postId);

    boolean existsByUserId(String userId);

    void deleteByUserId(String userId);
}
