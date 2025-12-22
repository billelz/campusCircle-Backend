package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.PostContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  // Indique que c'est un composant d'accès aux données
public interface PostContentRepository extends MongoRepository<PostContent, String> {

    Optional<PostContent> findByPostId(String postId);

    List<PostContent> findByViewCountGreaterThan(Long viewCount);

    List<PostContent> findByTrendingScoreGreaterThanOrderByTrendingScoreDesc(Double score);

    List<PostContent> findByBodyTextContaining(String keyword);

    void deleteByPostId(String postId);
}