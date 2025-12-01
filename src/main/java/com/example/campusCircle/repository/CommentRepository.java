package com.example.campusCircle.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.campusCircle.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> { }
