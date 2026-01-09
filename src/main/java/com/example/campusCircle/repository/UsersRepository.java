package com.example.campusCircle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.campusCircle.model.Users;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    
    Optional<Users> findByEmail(String email);
    
    Optional<Users> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Users u WHERE u.university.id = :universityId AND u.isActive = true")
    List<Users> findByUniversity(@Param("universityId") Long universityId);

    @Query("SELECT u FROM Users u WHERE u.isActive = true ORDER BY u.totalKarma DESC")
    Page<Users> findTopKarmaUsers(Pageable pageable);

    @Query("SELECT u FROM Users u WHERE (u.isActive IS NULL OR u.isActive = true) AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.realName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Users> searchUsers(@Param("query") String query);

    @Modifying
    @Query("UPDATE Users u SET u.postKarma = u.postKarma + :amount WHERE u.username = :username")
    void updatePostKarma(@Param("username") String username, @Param("amount") int amount);

    @Modifying
    @Query("UPDATE Users u SET u.commentKarma = u.commentKarma + :amount WHERE u.username = :username")
    void updateCommentKarma(@Param("username") String username, @Param("amount") int amount);

    @Modifying
    @Query("UPDATE Users u SET u.totalKarma = u.postKarma + u.commentKarma WHERE u.username = :username")
    void recalculateTotalKarma(@Param("username") String username);
}