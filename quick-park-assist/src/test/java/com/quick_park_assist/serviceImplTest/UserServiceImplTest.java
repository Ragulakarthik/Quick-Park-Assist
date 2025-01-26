package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.dto.UserProfileDTO;
import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setFullName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setUserType("Customer");
        dto.setPassword("password123");
        dto.setAddress("123 Main St");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFullName(dto.getFullName());
        savedUser.setEmail(dto.getEmail().toLowerCase());
        savedUser.setPhoneNumber(dto.getPhoneNumber());
        savedUser.setUserType(dto.getUserType());
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testIsEmailTaken() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = userService.isEmailTaken(email);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void testIsPhoneNumberTaken() {
        // Arrange
        String phoneNumber = "1234567890";
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        // Act
        boolean result = userService.isPhoneNumberTaken(phoneNumber);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByPhoneNumber(phoneNumber);
    }

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        User user = new User();
        user.setEmail(email);
        user.setPassword(userService.hashPassword(password)); // Hashing the password

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = userService.authenticateUser(email, password);

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testAuthenticateUser_Failure() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword(userService.hashPassword("password123"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = userService.authenticateUser(email, password);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateProfile() {
        // Arrange
        Long userId = 1L;
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setFullName("John Doe");
        profileDTO.setEmail("john.doe@example.com");
        profileDTO.setPhoneNumber("1234567890");
        profileDTO.setAddress("123 Main St");

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.updateProfile(userId, profileDTO);

        // Assert
        assertEquals(profileDTO.getFullName(), user.getFullName());
        assertEquals(profileDTO.getEmail(), user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeactivateAccount() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deactivateAccount(userId);

        // Assert
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteAccount() {
        // Arrange
        Long userId = 1L;

        // Act
        userService.deleteAccount(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }
}

