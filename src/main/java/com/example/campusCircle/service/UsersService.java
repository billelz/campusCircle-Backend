package com.example.campusCircle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Users;
import com.example.campusCircle.repository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public Users createUser(Users user) {
        return usersRepository.save(user);
    }

    public Users getUser(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users updateUser(Long id, Users updated) {
        Users existing = getUser(id);

        existing.setEmail(updated.getEmail());
        existing.setUsername(updated.getUsername());
        existing.setPasswordHash(updated.getPasswordHash());
        existing.setUniversity(updated.getUniversity());
        existing.setRealName(updated.getRealName());
        existing.setProfileVisibilitySettings(updated.getProfileVisibilitySettings());

        return usersRepository.save(existing);
    }

    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }
}

