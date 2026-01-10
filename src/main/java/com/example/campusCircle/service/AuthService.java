package com.example.campusCircle.service;

import com.example.campusCircle.dto.*;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.repository.UsersRepository;
import com.example.campusCircle.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(SignupRequest request) {
        // Check if username exists
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email exists
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Validate university email
        String email = request.getEmail().toLowerCase();
        if (!isValidUniversityEmail(email)) {
            throw new RuntimeException("Please use a valid university email (.edu, .university, or academic domain)");
        }

        // Create new user
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setVerificationStatus(Users.VerificationStatus.PENDING);
        user.setCreatedAt(LocalDateTime.now());

        usersRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .realName(user.getRealName())
                        .verificationStatus(user.getVerificationStatus() != null ? user.getVerificationStatus().name() : null)
                        .build())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        Users user = usersRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(),
                request.getUsernameOrEmail()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .realName(user.getRealName())
                        .verificationStatus(user.getVerificationStatus() != null ? user.getVerificationStatus().name() : null)
                        .build())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Token is not a refresh token");
        }

        String username = jwtTokenProvider.extractUsername(refreshToken);
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtTokenProvider.generateToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .realName(user.getRealName())
                        .verificationStatus(user.getVerificationStatus() != null ? user.getVerificationStatus().name() : null)
                        .build())
                .build();
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
    }

    public Users getCurrentUser(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean checkUsernameAvailability(String username) {
        return !usersRepository.existsByUsername(username);
    }

    public boolean checkEmailAvailability(String email) {
        return !usersRepository.existsByEmail(email);
    }

    /**
     * Validates if the email is a university email
     * Accepts: .edu, .university, .ac.XX (academic), .edu.XX
     */
    private boolean isValidUniversityEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        
        // Check for .edu domains
        if (domain.endsWith(".edu")) {
            return true;
        }
        // Check for .university domains
        if (domain.endsWith(".university")) {
            return true;
        }
        // Check for academic domains (.ac.uk, .ac.jp, etc.)
        if (domain.matches(".*\\.ac\\.[a-z]{2,3}$")) {
            return true;
        }
        // Check for .edu.XX domains (.edu.au, .edu.cn, etc.)
        if (domain.matches(".*\\.edu\\.[a-z]{2,3}$")) {
            return true;
        }
        return false;
    }
}
