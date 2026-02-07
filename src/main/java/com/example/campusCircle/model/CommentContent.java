package com.example.campusCircle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "comment_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentContent {
    
    @Id
    private String id;
    
    @Field("comment_id")
    private Long commentId;
    
    @Field("content")
    private String content;
    
    @Field("media_urls")
    private List<String> mediaUrls;

    // Convenience method to get commentId as String
    public String getCommentIdString() {
        return commentId != null ? String.valueOf(commentId) : null;
    }
}
