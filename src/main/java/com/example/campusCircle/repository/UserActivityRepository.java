package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.UserActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {
    
    Optional<UserActivity> findByUsername(String username);
    
    List<UserActivity> findByLastActiveAfter(LocalDateTime date);
    
    List<UserActivity> findByActiveChannelsContaining(String channel);
    
    void deleteByUsername(String username);
}
