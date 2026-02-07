package com.example.campusCircle.dto;

import com.example.campusCircle.model.Vote.ContentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {

    @NotNull(message = "Content ID is required")
    private Long contentId;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    @NotNull(message = "Vote value is required")
    private Integer voteValue; // +1 for upvote, -1 for downvote, 0 to remove vote
}
