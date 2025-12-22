package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "analytics_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {

    @Id
    private String id;

    @Field("event_type")
    private String eventType;

    @Field("username_hashed")
    private String usernameHashed;

    @Field("timestamp")
    private LocalDateTime timestamp;

    @Field("metadata")
    private Map<String, Object> metadata;
}
