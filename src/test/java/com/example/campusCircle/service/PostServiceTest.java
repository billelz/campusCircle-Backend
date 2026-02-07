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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.campusCircle.model.Post;
import com.example.campusCircle.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Unit Tests")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = Post.builder()
                .id(1L)
                .authorUsername("testuser")
                .channelId(1L)
                .title("Test Post Title")
                .upvoteCount(10)
                .downvoteCount(2)
                .commentCount(5)
                .isPinned(false)
                .isLocked(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createPost should set createdAt and save post")
    void createPost_ShouldSetCreatedAtAndSave() {
        // Arrange
        Post newPost = Post.builder()
                .authorUsername("testuser")
                .channelId(1L)
                .title("New Post")
                .build();

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        Post result = postService.createPost(newPost);

        // Assert
        assertNotNull(result.getCreatedAt());
        verify(postRepository).save(newPost);
    }

    @Test
    @DisplayName("getPostById should return post when exists")
    void getPostById_WhenExists_ShouldReturnPost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        Post result = postService.getPostById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testPost.getId(), result.getId());
        assertEquals(testPost.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("getPostById should throw exception when post not found")
    void getPostById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> postService.getPostById(99L));
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    @DisplayName("getAllActivePosts should return list of active posts")
    void getAllActivePosts_ShouldReturnActivePostsList() {
        // Arrange
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findAllActive()).thenReturn(posts);

        // Act
        List<Post> result = postService.getAllActivePosts();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("updatePost should update title and set editedAt")
    void updatePost_ShouldUpdateTitleAndSetEditedAt() {
        // Arrange
        Post updatedPost = Post.builder().title("Updated Title").build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Post result = postService.updatePost(1L, updatedPost);

        // Assert
        assertEquals("Updated Title", result.getTitle());
        assertNotNull(result.getEditedAt());
    }

    @Test
    @DisplayName("deletePost should set deletedAt timestamp")
    void deletePost_ShouldSetDeletedAt() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        postService.deletePost(1L);

        // Assert
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertNotNull(postCaptor.getValue().getDeletedAt());
    }

    @Test
    @DisplayName("incrementCommentCount should increment by one")
    void incrementCommentCount_ShouldIncrementByOne() {
        // Arrange
        testPost.setCommentCount(5);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        postService.incrementCommentCount(1L);

        // Assert
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(6, postCaptor.getValue().getCommentCount());
    }

    @Test
    @DisplayName("decrementCommentCount should decrement by one")
    void decrementCommentCount_ShouldDecrementByOne() {
        // Arrange
        testPost.setCommentCount(5);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        postService.decrementCommentCount(1L);

        // Assert
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(4, postCaptor.getValue().getCommentCount());
    }

    @Test
    @DisplayName("decrementCommentCount should not go below zero")
    void decrementCommentCount_ShouldNotGoBelowZero() {
        // Arrange
        testPost.setCommentCount(0);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        postService.decrementCommentCount(1L);

        // Assert
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(0, postCaptor.getValue().getCommentCount());
    }

    @Test
    @DisplayName("updateVoteCounts should update both upvote and downvote counts")
    void updateVoteCounts_ShouldUpdateBothCounts() {
        // Arrange
        testPost.setUpvoteCount(10);
        testPost.setDownvoteCount(2);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        postService.updateVoteCounts(1L, 5, 3);

        // Assert
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(15, postCaptor.getValue().getUpvoteCount());
        assertEquals(5, postCaptor.getValue().getDownvoteCount());
    }

    @Test
    @DisplayName("getPostsByChannel should return posts for channel")
    void getPostsByChannel_ShouldReturnPostsForChannel() {
        // Arrange
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByChannelIdAndDeletedAtIsNullOrderByCreatedAtDesc(1L)).thenReturn(posts);

        // Act
        List<Post> result = postService.getPostsByChannel(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getChannelId());
    }

    @Test
    @DisplayName("getPostsByAuthor should return posts for author")
    void getPostsByAuthor_ShouldReturnPostsForAuthor() {
        // Arrange
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByAuthorUsernameAndDeletedAtIsNullOrderByCreatedAtDesc("testuser"))
                .thenReturn(posts);

        // Act
        List<Post> result = postService.getPostsByAuthor("testuser");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("testuser", result.get(0).getAuthorUsername());
    }
}
