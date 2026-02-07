package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "user_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {
    
    @Id
    private String id;
    
    @Field("username")
    private String username;
    
    @Field("last_active")
    private LocalDateTime lastActive;
    
    @Field("active_channels")
    private List<String> activeChannels;
    
    @Field("viewing_history")
    private List<Map<String, Object>> viewingHistory;
}
