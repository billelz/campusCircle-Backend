package com.example.campusCircle.model.nosql;

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
    private String commentId;
    
    @Field("body_text")
    private String bodyText;
    
    @Field("media_urls")
    private List<String> mediaUrls;
}