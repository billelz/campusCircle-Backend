package com.example.campusCircle.repository;

import com.example.campusCircle.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsByUniversityIdAndName(Long universityId, String name);

    List<Channel> findByUniversityId(Long universityId);
}
