package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "search_index")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchIndex {
    
    @Id
    private String id;
    
    @Field("content_id")
    private String contentId;
    
    @Field("content_type")
    private String contentType; // "post" or "comment"
    
    @Field("indexed_text")
    private String indexedText;
    
    @Field("keywords")
    private List<String> keywords;
    
    @Field("author_username")
    private String authorUsername;
    
    @Field("channel")
    private String channel;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("search_score")
    private Double searchScore;
}
