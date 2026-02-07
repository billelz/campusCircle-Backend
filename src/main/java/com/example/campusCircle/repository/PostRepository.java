package com.example.campusCircle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.campusCircle.model.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Post> findAllActive();

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    Page<Post> findAllActive(Pageable pageable);

    List<Post> findByChannelIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long channelId);

    Page<Post> findByChannelIdAndDeletedAtIsNull(Long channelId, Pageable pageable);

    List<Post> findByAuthorUsernameAndDeletedAtIsNullOrderByCreatedAtDesc(String authorUsername);

    Page<Post> findByAuthorUsernameAndDeletedAtIsNull(String authorUsername, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL ORDER BY (p.upvoteCount - p.downvoteCount + p.commentCount * 2) DESC")
    List<Post> findTrendingPosts(@Param("limit") int limit);

    @Query("SELECT p FROM Post p WHERE p.channelId = :channelId AND p.isPinned = true AND p.deletedAt IS NULL")
    List<Post> findPinnedByChannel(@Param("channelId") Long channelId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.authorUsername = :username AND p.deletedAt IS NULL")
    Long countByAuthor(@Param("username") String username);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("query") String query);

    // Query for top contributors based on total upvotes on their posts
    @Query("SELECT p.authorUsername as username, SUM(p.upvoteCount) as totalUpvotes " +
           "FROM Post p WHERE p.deletedAt IS NULL " +
           "GROUP BY p.authorUsername " +
           "ORDER BY totalUpvotes DESC")
    List<Object[]> findTopContributorsByUpvotes();
}
