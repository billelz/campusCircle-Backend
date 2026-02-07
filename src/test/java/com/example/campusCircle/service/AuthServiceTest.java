package com.example.campusCircle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.campusCircle.dto.ChangePasswordRequest;
import com.example.campusCircle.dto.SignupRequest;
import com.example.campusCircle.dto.AuthResponse;
import com.example.campusCircle.model.University;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.repository.UniversityRepository;
import com.example.campusCircle.repository.UsersRepository;
import com.example.campusCircle.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UniversityRepository universityRepository;

    @InjectMocks
    private AuthService authService;

    private Users testUser;
    private SignupRequest signupRequest;
    private University testUniversity;

    @BeforeEach
    void setUp() {
        testUniversity = new University();
        testUniversity.setId(1L);
        testUniversity.setName("University edu");
        testUniversity.setDomain("university.edu");

        testUser = Users.builder()
                .id(1L)
                .username("testuser")
                .email("test@university.edu")
                .passwordHash("hashedPassword")
                .realName("Test User")
                .university(testUniversity)
                .verificationStatus(Users.VerificationStatus.PENDING)
                .build();

        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@university.edu");
        signupRequest.setPassword("password123");
        signupRequest.setRealName("New User");
    }

    @Test
    @DisplayName("register should throw exception when username already exists")
    void register_WhenUsernameExists_ShouldThrowException() {
        // Arrange
        when(usersRepository.existsByUsername("newuser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(signupRequest));
        assertEquals("Username is already taken", exception.getMessage());
        verify(usersRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should throw exception when email already exists")
    void register_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(usersRepository.existsByUsername("newuser")).thenReturn(false);
        when(usersRepository.existsByEmail("newuser@university.edu")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(signupRequest));
        assertEquals("Email is already registered", exception.getMessage());
        verify(usersRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should throw exception when email is not a university email")
    void register_WhenInvalidEmail_ShouldThrowException() {
        // Arrange
        signupRequest.setEmail("invalid@gmail.com");
        when(usersRepository.existsByUsername("newuser")).thenReturn(false);
        when(usersRepository.existsByEmail("invalid@gmail.com")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(signupRequest));
        assertTrue(exception.getMessage().contains("valid university"));
        verify(usersRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should create user and return tokens when valid")
    void register_WhenValid_ShouldCreateUserAndReturnTokens() {
        // Arrange
        when(usersRepository.existsByUsername("newuser")).thenReturn(false);
        when(usersRepository.existsByEmail("newuser@university.edu")).thenReturn(false);
        when(universityRepository.findByDomain("university.edu")).thenReturn(Optional.of(testUniversity));
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenProvider.getExpirationTime()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.register(signupRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(usersRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("register should accept .edu emails")
    void register_WithEduEmail_ShouldSucceed() {
        // Arrange
        signupRequest.setEmail("student@harvard.edu");
        when(usersRepository.existsByUsername("newuser")).thenReturn(false);
        when(usersRepository.existsByEmail("student@harvard.edu")).thenReturn(false);
        when(universityRepository.findByDomain("harvard.edu")).thenReturn(Optional.of(testUniversity));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> {
            Users u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refresh");
        when(jwtTokenProvider.getExpirationTime()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.register(signupRequest);

        // Assert
        assertNotNull(response);
        verify(usersRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("register should accept .ac.uk academic emails")
    void register_WithAcUkEmail_ShouldSucceed() {
        // Arrange
        signupRequest.setEmail("student@oxford.ac.uk");
        when(usersRepository.existsByUsername("newuser")).thenReturn(false);
        when(usersRepository.existsByEmail("student@oxford.ac.uk")).thenReturn(false);
        when(universityRepository.findByDomain("oxford.ac.uk")).thenReturn(Optional.of(testUniversity));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> {
            Users u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refresh");
        when(jwtTokenProvider.getExpirationTime()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.register(signupRequest);

        // Assert
        assertNotNull(response);
    }

    @Test
    @DisplayName("checkUsernameAvailability should return false when username is taken")
    void checkUsernameAvailability_WhenTaken_ShouldReturnFalse() {
        // Arrange
        when(usersRepository.existsByUsername("takenuser")).thenReturn(true);

        // Act
        boolean result = authService.checkUsernameAvailability("takenuser");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("checkUsernameAvailability should return true when username is available")
    void checkUsernameAvailability_WhenAvailable_ShouldReturnTrue() {
        // Arrange
        when(usersRepository.existsByUsername("availableuser")).thenReturn(false);

        // Act
        boolean result = authService.checkUsernameAvailability("availableuser");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("checkEmailAvailability should return false when email is taken")
    void checkEmailAvailability_WhenTaken_ShouldReturnFalse() {
        // Arrange
        when(usersRepository.existsByEmail("taken@edu.com")).thenReturn(true);

        // Act
        boolean result = authService.checkEmailAvailability("taken@edu.com");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("checkEmailAvailability should return true when email is available")
    void checkEmailAvailability_WhenAvailable_ShouldReturnTrue() {
        // Arrange
        when(usersRepository.existsByEmail("available@edu.com")).thenReturn(false);

        // Act
        boolean result = authService.checkEmailAvailability("available@edu.com");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("changePassword should throw exception when current password is incorrect")
    void changePassword_WhenCurrentPasswordIncorrect_ShouldThrowException() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123");

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.changePassword("testuser", request));
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(usersRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword should update password when current password is correct")
    void changePassword_WhenValid_ShouldUpdatePassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("correctPassword");
        request.setNewPassword("newPassword123");

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("correctPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        authService.changePassword("testuser", request);

        // Assert
        verify(usersRepository).save(any(Users.class));
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    @DisplayName("getCurrentUser should return user when found")
    void getCurrentUser_ShouldReturnUser() {
        // Arrange
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Users result = authService.getCurrentUser("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("getCurrentUser should throw exception when user not found")
    void getCurrentUser_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(usersRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.getCurrentUser("unknown"));
        assertEquals("User not found", exception.getMessage());
    }
}
