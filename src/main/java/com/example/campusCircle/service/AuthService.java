package com.example.campusCircle.service;

import com.example.campusCircle.dto.*;
import com.example.campusCircle.model.University;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.repository.UniversityRepository;
import com.example.campusCircle.repository.UsersRepository;
import com.example.campusCircle.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Common personal email domains that are NOT allowed
    private static final List<String> BLOCKED_DOMAINS = Arrays.asList(
        "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "live.com",
        "aol.com", "icloud.com", "mail.com", "protonmail.com", "zoho.com",
        "yandex.com", "gmx.com", "fastmail.com", "tutanota.com"
    );

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
            throw new RuntimeException("Please use a valid university or educational institution email. Personal email domains (Gmail, Yahoo, etc.) are not accepted.");
        }

        // Extract domain and find/create university
        String domain = extractDomain(email);
        University university = findOrCreateUniversity(domain);

        // Create new user
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setUniversity(university);
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
                        .universityId(university.getId())
                        .universityName(university.getName())
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
                        .universityId(user.getUniversity() != null ? user.getUniversity().getId() : null)
                        .universityName(user.getUniversity() != null ? user.getUniversity().getName() : null)
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
                        .universityId(user.getUniversity() != null ? user.getUniversity().getId() : null)
                        .universityName(user.getUniversity() != null ? user.getUniversity().getName() : null)
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
     * Validates if the email is a university/institutional email
     * Rejects common personal email domains
     */
    private boolean isValidUniversityEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = extractDomain(email);
        
        // Reject blocked personal email domains
        if (BLOCKED_DOMAINS.contains(domain)) {
            return false;
        }
        
        // Accept any other domain (university, company, institution)
        return true;
    }

    /**
     * Extracts domain from email address
     */
    private String extractDomain(String email) {
        return email.substring(email.indexOf("@") + 1).toLowerCase();
    }

    /**
     * Extracts university name from domain
     * e.g., horizon-university.tn -> Horizon University
     * e.g., mit.edu -> MIT
     * e.g., stanford.edu -> Stanford
     */
    private String extractUniversityName(String domain) {
        // Remove TLD (e.g., .edu, .tn, .com, etc.)
        String name = domain;
        int lastDot = name.lastIndexOf(".");
        if (lastDot > 0) {
            name = name.substring(0, lastDot);
        }
        
        // Handle .edu.XX or .ac.XX domains
        if (name.endsWith(".edu") || name.endsWith(".ac")) {
            lastDot = name.lastIndexOf(".");
            if (lastDot > 0) {
                name = name.substring(0, lastDot);
            }
        }

        // Replace hyphens and underscores with spaces
        name = name.replace("-", " ").replace("_", " ");
        
        // Capitalize each word
        String[] words = name.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                // Check if it's an acronym (all uppercase or common acronyms)
                if (word.length() <= 4 && word.matches("[a-zA-Z]+")) {
                    result.append(word.toUpperCase());
                } else {
                    result.append(Character.toUpperCase(word.charAt(0)));
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }
        
        return result.toString().trim();
    }

    /**
     * Finds existing university by domain or creates a new one
     */
    private University findOrCreateUniversity(String domain) {
        return universityRepository.findByDomain(domain)
                .orElseGet(() -> {
                    University newUniversity = new University();
                    newUniversity.setDomain(domain);
                    newUniversity.setName(extractUniversityName(domain));
                    newUniversity.setActiveStatus(true);
                    newUniversity.setStudentCount(1);
                    return universityRepository.save(newUniversity);
                });
    }
}
