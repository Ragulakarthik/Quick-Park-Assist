package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.dto.UserProfileDTO;
import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import com.quick_park_assist.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Autowired
    private IOTPService otpService;
    private User activeUser;
    private User inactiveUser;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() throws UserServiceImpl.PasswordHashingException {
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
    void testAuthenticateUser_Success() throws UserServiceImpl.PasswordHashingException {
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
    void testAuthenticateUser_Failure() throws UserServiceImpl.PasswordHashingException {
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
    // Test for isAccountActive method
    @Test
    void isAccountActive_userIsActive() {
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setEmail("active@example.com");
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActive(false);
        when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(activeUser));

        boolean result = userService.isAccountActive("active@example.com");

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("active@example.com");
    }

    @Test
    void isAccountActive_userIsInactive() {
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setEmail("active@example.com");
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActive(false);
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactiveUser));

        boolean result = userService.isAccountActive("inactive@example.com");

        assertFalse(result);
        verify(userRepository, times(1)).findByEmail("inactive@example.com");
    }

    @Test
    void isAccountActive_userDoesNotExist() {
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setEmail("active@example.com");
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActive(false);
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        boolean result = userService.isAccountActive("unknown@example.com");

        assertFalse(result);
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    // Test for reactivateAccount method
    @Test
    void reactivateAccount_userReactivatedSuccessfully() {
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setEmail("active@example.com");
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActive(false);
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactiveUser));

        userService.reactivateAccount("inactive@example.com");

        assertTrue(inactiveUser.isActive()); // User should now be active
        verify(userRepository, times(1)).save(inactiveUser);
    }

    @Test
    void reactivateAccount_userAlreadyActive() {
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setEmail("active@example.com");
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActive(false);
        when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(activeUser));

        userService.reactivateAccount("active@example.com");

        assertTrue(activeUser.isActive()); // User remains active
        verify(userRepository, times(1)).save(activeUser);
    }

    @Test
    void reactivateAccount_userDoesNotExist() {
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setEmail("active@example.com");
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActive(false);
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.reactivateAccount("unknown@example.com"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
        verify(userRepository, times(0)).save(any(User.class)); // Ensure no save attempt was made
    }
}

