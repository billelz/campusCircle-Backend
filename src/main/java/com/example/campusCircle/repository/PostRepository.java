package com.example.campusCircle.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.campusCircle.model.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    List<Post> findAllActive();
}

