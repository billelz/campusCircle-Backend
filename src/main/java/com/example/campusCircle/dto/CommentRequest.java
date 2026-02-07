package com.example.campusCircle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotNull(message = "Post ID is required")
    private Long postId;

    private Long parentCommentId;

    @NotBlank(message = "Comment content is required")
    @Size(max = 10000, message = "Comment cannot exceed 10000 characters")
    private String content;

    private List<String> mediaUrls;
}
