package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.DirectMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectMessageRepository extends MongoRepository<DirectMessage, String> {
    
    Optional<DirectMessage> findByConversationId(String conversationId);
    
    @Query("{ 'participants': ?0 }")
    List<DirectMessage> findByParticipant(String username);
    
    @Query("{ 'participants': { $all: [?0, ?1] } }")
    Optional<DirectMessage> findConversationBetweenUsers(String user1, String user2);
    
    @Query(value = "{ 'participants': ?0 }", sort = "{ 'last_message_at': -1 }")
    List<DirectMessage> findConversationsByUserSorted(String username);
}
