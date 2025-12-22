package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "post_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostContent {
    
    @Id
    private String id;
    
    @Field("post_id")
    private String postId;
    
    @Field("body_text")
    private String bodyText;
    
    @Field("media_urls")
    private List<String> mediaUrls;
    
    @Field("poll_data")
    private Map<String, Object> pollData;
    
    @Field("link_metadata")
    private Map<String, Object> linkMetadata;
    
    @Field("view_count")
    private Long viewCount;
    
    @Field("trending_score")
    private Double trendingScore;
}