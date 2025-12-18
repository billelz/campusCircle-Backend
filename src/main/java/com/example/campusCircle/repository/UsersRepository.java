package com.example.campusCircle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.campusCircle.model.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}

