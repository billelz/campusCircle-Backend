package com.example.campusCircle.dto;

import com.example.campusCircle.model.Channel.ChannelCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRequest {

    @NotBlank(message = "Channel name is required")
    @Size(min = 3, max = 100, message = "Channel name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 2000, message = "Rules cannot exceed 2000 characters")
    private String rules;

    private Long universityId;

    private ChannelCategory category = ChannelCategory.GENERAL;
}
