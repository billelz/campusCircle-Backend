package com.example.campusCircle.controller;

import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.Comment;
import com.example.campusCircle.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment create(@RequestBody Comment comment) {
        return commentService.createComment(comment);
    }

    @GetMapping("/{id}")
    public Comment getOne(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @GetMapping
    public List<Comment> getAll() {
        return commentService.getAllComments();
    }

    @PutMapping("/{id}")
    public Comment update(@PathVariable Long id, @RequestBody Comment updated) {
        return commentService.updateComment(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
