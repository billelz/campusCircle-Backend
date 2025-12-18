package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.UserPreference;
import com.example.campusCircle.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserPreferenceService {

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    public List<UserPreference> getAllUserPreferences() {
        return userPreferenceRepository.findAll();
    }

    public Optional<UserPreference> getUserPreferenceByUsername(String username) {
        return userPreferenceRepository.findByUsername(username);
    }

    public UserPreference saveUserPreference(UserPreference userPreference) {
        return userPreferenceRepository.save(userPreference);
    }

    public void deleteUserPreference(String id) {
        userPreferenceRepository.deleteById(id);
    }
}
