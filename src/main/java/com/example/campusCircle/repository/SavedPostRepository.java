package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.SavedPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedPostRepository extends MongoRepository<SavedPost, String> {
    
    Optional<SavedPost> findByUserId(Long userId);
    
    Optional<SavedPost> findByUsername(String username);
    
    void deleteByUserId(Long userId);
}
