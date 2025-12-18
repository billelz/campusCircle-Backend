package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.UserActivity;
import com.example.campusCircle.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    public List<UserActivity> getAllUserActivities() {
        return userActivityRepository.findAll();
    }

    public Optional<UserActivity> getUserActivityByUsername(String username) {
        return userActivityRepository.findByUsername(username);
    }

    public Optional<UserActivity> getUserActivityById(String id) {
        return userActivityRepository.findById(id);
    }

    public List<UserActivity> getActiveUsersSince(LocalDateTime date) {
        return userActivityRepository.findByLastActiveAfter(date);
    }

    public List<UserActivity> getUsersByChannel(String channel) {
        return userActivityRepository.findByActiveChannelsContaining(channel);
    }

    public UserActivity saveUserActivity(UserActivity userActivity) {
        return userActivityRepository.save(userActivity);
    }

    public UserActivity updateLastActive(String username) {
        Optional<UserActivity> activity = userActivityRepository.findByUsername(username);
        if (activity.isPresent()) {
            UserActivity userActivity = activity.get();
            userActivity.setLastActive(LocalDateTime.now());
            return userActivityRepository.save(userActivity);
        }
        return null;
    }

    public void deleteUserActivity(String id) {
        userActivityRepository.deleteById(id);
    }

    public void deleteUserActivityByUsername(String username) {
        userActivityRepository.deleteByUsername(username);
    }
}
