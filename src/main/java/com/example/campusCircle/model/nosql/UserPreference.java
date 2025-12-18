package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    
    @Id
    private String id;
    
    @Field("username")
    private String username;
    
    @Field("feed_settings")
    private Map<String, Object> feedSettings;
    
    @Field("notification_preferences")
    private Map<String, Object> notificationPreferences;
    
    @Field("content_filters")
    private Map<String, Object> contentFilters;
}
