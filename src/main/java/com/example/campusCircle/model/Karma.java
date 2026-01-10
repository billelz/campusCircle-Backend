package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "karma")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Karma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "karma_score")
    private Integer karmaScore = 0;

    @Column(name = "post_karma")
    private Integer postKarma = 0;

    @Column(name = "comment_karma")
    private Integer commentKarma = 0;

    @ElementCollection
    @CollectionTable(name = "karma_by_channel", joinColumns = @JoinColumn(name = "karma_id"))
    @MapKeyColumn(name = "channel_id")
    @Column(name = "channel_karma")
    private Map<Long, Integer> karmaByChannel = new HashMap<>();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        karmaScore = postKarma + commentKarma;
    }

    public void addPostKarma(int amount) {
        this.postKarma += amount;
        this.karmaScore = this.postKarma + this.commentKarma;
    }

    public void addCommentKarma(int amount) {
        this.commentKarma += amount;
        this.karmaScore = this.postKarma + this.commentKarma;
    }

    public void addChannelKarma(Long channelId, int amount) {
        this.karmaByChannel.merge(channelId, amount, Integer::sum);
    }
}
