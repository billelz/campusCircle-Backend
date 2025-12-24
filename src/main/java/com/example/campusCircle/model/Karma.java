package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "karma")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Karma {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    private Long karmaScore;

    @ElementCollection
    @CollectionTable(name = "karma_by_channel", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "channel_id")
    @Column(name = "score")
    private Map<Long, Integer> karmaByChannel = new HashMap<>();
}
