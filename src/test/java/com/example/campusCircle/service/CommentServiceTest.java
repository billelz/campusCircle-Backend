package com.example.campusCircle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.campusCircle.model.Comment;
import com.example.campusCircle.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService Unit Tests")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    private CommentService commentService;

    private Comment testComment;
    private Comment replyComment;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postService);

        testComment = Comment.builder()
                .id(1L)
                .postId(1L)
                .authorUsername("testuser")
                .upvoteCount(5)
                .downvoteCount(1)
                .replyCount(2)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        replyComment = Comment.builder()
                .id(2L)
                .postId(1L)
                .parentCommentId(1L)
                .authorUsername("replyuser")
                .upvoteCount(0)
                .downvoteCount(0)
                .replyCount(0)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createComment should initialize fields and increment post comment count")
    void createComment_ShouldInitializeFieldsAndIncrementPostCount() {
        // Arrange
        Comment newComment = Comment.builder()
                .postId(1L)
                .authorUsername("testuser")
                .build();

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        doNothing().when(postService).incrementCommentCount(1L);

        // Act
        Comment result = commentService.createComment(newComment);

        // Assert
        assertNotNull(result.getCreatedAt());
        assertEquals(0, result.getUpvoteCount());
        assertEquals(0, result.getDownvoteCount());
        assertEquals(0, result.getReplyCount());
        assertFalse(result.getIsDeleted());
        verify(postService).incrementCommentCount(1L);
    }

    @Test
    @DisplayName("createComment with parent should increment parent reply count")
    void createComment_WithParent_ShouldIncrementParentReplyCount() {
        // Arrange
        Comment reply = Comment.builder()
                .postId(1L)
                .parentCommentId(1L)
                .authorUsername("replyuser")
                .build();

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        doNothing().when(postService).incrementCommentCount(1L);

        // Act
        commentService.createComment(reply);

        // Assert
        verify(postService).incrementCommentCount(1L);
        verify(commentRepository, times(2)).save(any(Comment.class)); // Once for comment, once for parent
    }

    @Test
    @DisplayName("getCommentById should return comment when exists")
    void getCommentById_WhenExists_ShouldReturnComment() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // Act
        Comment result = commentService.getCommentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testComment.getId(), result.getId());
    }

    @Test
    @DisplayName("getCommentById should throw exception when not found")
    void getCommentById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> commentService.getCommentById(99L));
        assertEquals("Comment not found", exception.getMessage());
    }

    @Test
    @DisplayName("getCommentsByPost should return comments for post")
    void getCommentsByPost_ShouldReturnCommentsForPost() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        when(commentRepository.findTopLevelByPostId(1L)).thenReturn(comments);

        // Act
        List<Comment> result = commentService.getCommentsByPost(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("updateComment should set editedAt")
    void updateComment_ShouldSetEditedAt() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Comment result = commentService.updateComment(1L, "Updated content");

        // Assert
        assertNotNull(result.getEditedAt());
    }

    @Test
    @DisplayName("deleteComment should set deleted and decrement post comment count")
    void deleteComment_ShouldSetDeletedAndDecrementPostCount() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(postService).decrementCommentCount(1L);

        // Act
        commentService.deleteComment(1L);

        // Assert
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        assertTrue(commentCaptor.getValue().getIsDeleted());
        assertNotNull(commentCaptor.getValue().getDeletedAt());
        verify(postService).decrementCommentCount(1L);
    }

    @Test
    @DisplayName("deleteComment with parent should decrement parent reply count")
    void deleteComment_WithParent_ShouldDecrementParentReplyCount() {
        // Arrange
        when(commentRepository.findById(2L)).thenReturn(Optional.of(replyComment));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(postService).decrementCommentCount(1L);

        // Act
        commentService.deleteComment(2L);

        // Assert
        verify(commentRepository, times(2)).save(any(Comment.class)); // Once for delete, once for parent
    }

    @Test
    @DisplayName("updateVoteCounts should update both upvote and downvote counts")
    void updateVoteCounts_ShouldUpdateBothCounts() {
        // Arrange
        testComment.setUpvoteCount(5);
        testComment.setDownvoteCount(1);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        commentService.updateVoteCounts(1L, 3, 2);

        // Assert
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(8, commentCaptor.getValue().getUpvoteCount());
        assertEquals(3, commentCaptor.getValue().getDownvoteCount());
    }

    @Test
    @DisplayName("incrementReplyCount should increment by one")
    void incrementReplyCount_ShouldIncrementByOne() {
        // Arrange
        testComment.setReplyCount(2);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        commentService.incrementReplyCount(1L);

        // Assert
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(3, commentCaptor.getValue().getReplyCount());
    }

    @Test
    @DisplayName("decrementReplyCount should not go below zero")
    void decrementReplyCount_ShouldNotGoBelowZero() {
        // Arrange
        testComment.setReplyCount(0);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        commentService.decrementReplyCount(1L);

        // Assert
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(0, commentCaptor.getValue().getReplyCount());
    }

    @Test
    @DisplayName("getReplies should return replies for parent comment")
    void getReplies_ShouldReturnRepliesForParent() {
        // Arrange
        List<Comment> replies = Arrays.asList(replyComment);
        when(commentRepository.findRepliesByParentId(1L)).thenReturn(replies);

        // Act
        List<Comment> result = commentService.getReplies(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getParentCommentId());
    }
}
