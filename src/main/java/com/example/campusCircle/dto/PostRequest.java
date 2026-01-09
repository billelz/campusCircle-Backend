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
public class PostRequest {

    @NotNull(message = "Channel ID is required")
    private Long channelId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 300, message = "Title must be between 3 and 300 characters")
    private String title;

    @Size(max = 40000, message = "Content cannot exceed 40000 characters")
    private String content;

    private List<String> mediaUrls;

    private PollData poll;

    private String linkUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PollData {
        private String question;
        private List<String> options;
        private Integer durationHours = 24;
    }
}
