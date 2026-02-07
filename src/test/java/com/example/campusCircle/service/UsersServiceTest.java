package com.example.campusCircle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.campusCircle.model.Users;
import com.example.campusCircle.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsersService Unit Tests")
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService usersService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .id(1L)
                .username("testuser")
                .email("test@university.edu")
                .passwordHash("hashedPassword")
                .realName("Test User")
                .totalKarma(100)
                .postKarma(60)
                .commentKarma(40)
                .isActive(true)
                .isBanned(false)
                .verificationStatus(Users.VerificationStatus.PENDING)
                .profileVisibility(Users.ProfileVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createUser should save and return user")
    void createUser_ShouldSaveAndReturnUser() {
        // Arrange
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);

        // Act
        Users result = usersService.createUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(usersRepository).save(testUser);
    }

    @Test
    @DisplayName("getUser should return user when exists")
    void getUser_WhenExists_ShouldReturnUser() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Users result = usersService.getUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    @DisplayName("getUser should throw exception when user not found")
    void getUser_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usersService.getUser(99L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("getUserByUsername should return user")
    void getUserByUsername_ShouldReturnUser() {
        // Arrange
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Users result = usersService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("getUserByEmail should return user")
    void getUserByEmail_ShouldReturnUser() {
        // Arrange
        when(usersRepository.findByEmail("test@university.edu")).thenReturn(Optional.of(testUser));

        // Act
        Users result = usersService.getUserByEmail("test@university.edu");

        // Assert
        assertNotNull(result);
        assertEquals("test@university.edu", result.getEmail());
    }

    @Test
    @DisplayName("getAllUsers should return list of users")
    void getAllUsers_ShouldReturnUsersList() {
        // Arrange
        List<Users> users = Arrays.asList(testUser);
        when(usersRepository.findAll()).thenReturn(users);

        // Act
        List<Users> result = usersService.getAllUsers();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("updateUser should only update provided fields")
    void updateUser_ShouldOnlyUpdateProvidedFields() {
        // Arrange
        Users updatedUser = Users.builder()
                .realName("Updated Name")
                .bio("New bio")
                .build();
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Users result = usersService.updateUser(1L, updatedUser);

        // Assert
        assertEquals("Updated Name", result.getRealName());
        assertEquals("New bio", result.getBio());
        assertEquals("testuser", result.getUsername()); // Unchanged
        assertEquals("test@university.edu", result.getEmail()); // Email can be updated
    }

    @Test
    @DisplayName("deleteUser should set isActive to false")
    void deleteUser_ShouldSetIsActiveFalse() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        usersService.deleteUser(1L);

        // Assert
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(userCaptor.capture());
        assertFalse(userCaptor.getValue().getIsActive());
    }

    @Test
    @DisplayName("banUser should set ban fields correctly")
    void banUser_ShouldSetBanFields() {
        // Arrange
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        usersService.banUser(1L, "Violation of rules", expiresAt);

        // Assert
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().getIsBanned());
        assertEquals("Violation of rules", userCaptor.getValue().getBanReason());
        assertEquals(expiresAt, userCaptor.getValue().getBanExpiresAt());
    }

    @Test
    @DisplayName("unbanUser should clear ban fields")
    void unbanUser_ShouldClearBanFields() {
        // Arrange
        testUser.setIsBanned(true);
        testUser.setBanReason("Some reason");
        testUser.setBanExpiresAt(LocalDateTime.now().plusDays(7));
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        usersService.unbanUser(1L);

        // Assert
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(userCaptor.capture());
        assertFalse(userCaptor.getValue().getIsBanned());
        assertNull(userCaptor.getValue().getBanReason());
        assertNull(userCaptor.getValue().getBanExpiresAt());
    }

    @Test
    @DisplayName("verifyUser should set status to VERIFIED")
    void verifyUser_ShouldSetStatusToVerified() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        usersService.verifyUser(1L);

        // Assert
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(userCaptor.capture());
        assertEquals(Users.VerificationStatus.VERIFIED, userCaptor.getValue().getVerificationStatus());
    }

    @Test
    @DisplayName("isUserBanned should return false when user is not banned")
    void isUserBanned_WhenNotBanned_ShouldReturnFalse() {
        // Arrange
        testUser.setIsBanned(false);
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = usersService.isUserBanned("testuser");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isUserBanned should return true when banned and not expired")
    void isUserBanned_WhenBannedAndNotExpired_ShouldReturnTrue() {
        // Arrange
        testUser.setIsBanned(true);
        testUser.setBanExpiresAt(LocalDateTime.now().plusDays(7));
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = usersService.isUserBanned("testuser");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isUserBanned should unban and return false when ban expired")
    void isUserBanned_WhenBanExpired_ShouldUnbanAndReturnFalse() {
        // Arrange
        testUser.setIsBanned(true);
        testUser.setBanExpiresAt(LocalDateTime.now().minusDays(1)); // Expired
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        boolean result = usersService.isUserBanned("testuser");

        // Assert
        assertFalse(result);
        verify(usersRepository).save(any(Users.class)); // Should have called unban
    }

    @Test
    @DisplayName("updateLastLogin should set lastLoginAt")
    void updateLastLogin_ShouldSetLastLoginAt() {
        // Arrange
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        usersService.updateLastLogin("testuser");

        // Assert
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getLastLoginAt());
    }
}
