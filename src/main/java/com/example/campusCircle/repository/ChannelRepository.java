package com.example.campusCircle.repository;

import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Channel.ChannelCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsByUniversityIdAndName(Long universityId, String name);

    List<Channel> findByUniversityId(Long universityId);

    Optional<Channel> findByName(String name);

    @Query("SELECT c FROM Channel c WHERE c.isActive = true ORDER BY c.subscriberCount DESC")
    List<Channel> findActiveChannelsOrderBySubscribers();

    @Query("SELECT c FROM Channel c WHERE c.isActive = true ORDER BY c.subscriberCount DESC")
    Page<Channel> findActiveChannels(Pageable pageable);

    @Query("SELECT c FROM Channel c WHERE c.universityId = :universityId AND c.isActive = true ORDER BY c.subscriberCount DESC")
    List<Channel> findActiveByUniversity(@Param("universityId") Long universityId);

    @Query("SELECT c FROM Channel c WHERE c.category = :category AND c.isActive = true ORDER BY c.subscriberCount DESC")
    List<Channel> findByCategory(@Param("category") ChannelCategory category);

    @Query("SELECT c FROM Channel c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Channel> searchChannels(@Param("query") String query);

    @Query("SELECT c FROM Channel c WHERE c.isActive = true ORDER BY c.createdAt DESC")
    List<Channel> findRecentChannels(Pageable pageable);
}

