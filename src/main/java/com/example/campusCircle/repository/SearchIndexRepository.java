package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.SearchIndex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SearchIndexRepository extends MongoRepository<SearchIndex, String> {
    
    Optional<SearchIndex> findByContentId(String contentId);
    
    List<SearchIndex> findByContentType(String contentType);
    
    List<SearchIndex> findByAuthorUsername(String authorUsername);
    
    List<SearchIndex> findByChannel(String channel);
    
    List<SearchIndex> findByKeywordsContaining(String keyword);
    
    List<SearchIndex> findByIndexedTextContaining(String text);
    
    List<SearchIndex> findByCreatedAtAfter(LocalDateTime date);
    
    void deleteByContentId(String contentId);
}
