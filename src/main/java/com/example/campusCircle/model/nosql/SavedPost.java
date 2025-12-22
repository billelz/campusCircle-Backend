package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "saved_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedPost {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("post_ids")
    private List<String> postIds;

    @Field("saved_at")
    private LocalDateTime savedAt;
}
