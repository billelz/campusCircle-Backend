package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "saved_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedPost {
    
    @Id
    private String id;
    
    @Field("user_id")
    private Long userId;
    
    @Field("username")
    private String username;
    
    @Field("saved_items")
    private List<SavedItem> savedItems = new ArrayList<>();
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavedItem {
        private Long postId;
        private String postTitle;
        private Long channelId;
        private String channelName;
        private LocalDateTime savedAt;
        private String folder; // optional folder/category
    }
    
    public void addSavedItem(SavedItem item) {
        if (this.savedItems == null) {
            this.savedItems = new ArrayList<>();
        }
        // Remove if already exists
        this.savedItems.removeIf(i -> i.getPostId().equals(item.getPostId()));
        this.savedItems.add(item);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeSavedItem(Long postId) {
        if (this.savedItems != null) {
            this.savedItems.removeIf(i -> i.getPostId().equals(postId));
            this.updatedAt = LocalDateTime.now();
        }
    }
}
