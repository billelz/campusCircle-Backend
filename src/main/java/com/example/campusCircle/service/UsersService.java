package com.example.campusCircle.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Users getUserByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public List<Users> getUsersByUniversity(Long universityId) {
        return usersRepository.findByUniversity(universityId);
    }

    public Page<Users> getTopKarmaUsers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return usersRepository.findTopKarmaUsers(pageable);
    }

    public List<Users> searchUsers(String query) {
        return usersRepository.searchUsers(query);
    }

    public Users updateUser(Long id, Users updated) {
        Users existing = getUser(id);

        if (updated.getEmail() != null) {
            existing.setEmail(updated.getEmail());
        }
        if (updated.getRealName() != null) {
            existing.setRealName(updated.getRealName());
        }
        if (updated.getProfilePictureUrl() != null) {
            existing.setProfilePictureUrl(updated.getProfilePictureUrl());
        }
        if (updated.getBio() != null) {
            existing.setBio(updated.getBio());
        }
        if (updated.getGraduationYear() != null) {
            existing.setGraduationYear(updated.getGraduationYear());
        }
        if (updated.getMajor() != null) {
            existing.setMajor(updated.getMajor());
        }
        if (updated.getProfileVisibility() != null) {
            existing.setProfileVisibility(updated.getProfileVisibility());
        }

        return usersRepository.save(existing);
    }

    public void deleteUser(Long id) {
        Users user = getUser(id);
        user.setIsActive(false);
        usersRepository.save(user);
    }

    @Transactional
    public void updatePostKarma(String username, int amount) {
        usersRepository.updatePostKarma(username, amount);
        usersRepository.recalculateTotalKarma(username);
    }

    @Transactional
    public void updateCommentKarma(String username, int amount) {
        usersRepository.updateCommentKarma(username, amount);
        usersRepository.recalculateTotalKarma(username);
    }

    public void updateLastLogin(String username) {
        Users user = getUserByUsername(username);
        user.setLastLoginAt(LocalDateTime.now());
        usersRepository.save(user);
    }

    public void banUser(Long userId, String reason, LocalDateTime expiresAt) {
        Users user = getUser(userId);
        user.setIsBanned(true);
        user.setBanReason(reason);
        user.setBanExpiresAt(expiresAt);
        usersRepository.save(user);
    }

    public void unbanUser(Long userId) {
        Users user = getUser(userId);
        user.setIsBanned(false);
        user.setBanReason(null);
        user.setBanExpiresAt(null);
        usersRepository.save(user);
    }

    public void verifyUser(Long userId) {
        Users user = getUser(userId);
        user.setVerificationStatus(Users.VerificationStatus.VERIFIED);
        usersRepository.save(user);
    }

    public boolean isUserBanned(String username) {
        Users user = getUserByUsername(username);
        if (!user.getIsBanned()) {
            return false;
        }
        // Check if ban has expired
        if (user.getBanExpiresAt() != null && user.getBanExpiresAt().isBefore(LocalDateTime.now())) {
            unbanUser(user.getId());
            return false;
        }
        return true;
    }
}