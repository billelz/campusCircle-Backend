package com.example.campusCircle.repository;

import com.example.campusCircle.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByUserIdAndContentIdAndContentType(Long userId, Long contentId, Vote.ContentType contentType);
    
    List<Vote> findByContentIdAndContentType(Long contentId, Vote.ContentType contentType);
    
    List<Vote> findByUserId(Long userId);
    
    @Query("SELECT SUM(v.voteValue) FROM Vote v WHERE v.contentId = :contentId AND v.contentType = :contentType")
    Integer getVoteCount(@Param("contentId") Long contentId, @Param("contentType") Vote.ContentType contentType);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.contentId = :contentId AND v.contentType = :contentType AND v.voteValue = 1")
    Long countUpvotes(@Param("contentId") Long contentId, @Param("contentType") Vote.ContentType contentType);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.contentId = :contentId AND v.contentType = :contentType AND v.voteValue = -1")
    Long countDownvotes(@Param("contentId") Long contentId, @Param("contentType") Vote.ContentType contentType);
    
    void deleteByUserIdAndContentIdAndContentType(Long userId, Long contentId, Vote.ContentType contentType);
}
