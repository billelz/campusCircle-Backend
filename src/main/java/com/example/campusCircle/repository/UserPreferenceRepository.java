package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.UserPreference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends MongoRepository<UserPreference, String> {
    
    Optional<UserPreference> findByUsername(String username);
    
    void deleteByUsername(String username);
}
