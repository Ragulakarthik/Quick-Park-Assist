package com.quick_park_assist.DTOTest;

import com.quick_park_assist.dto.UserProfileDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileDTOTest {

    @Test
    void testValidUserProfileDTO() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("John Doe");
        userProfileDTO.setEmail("johndoe@example.com");
        userProfileDTO.setPhoneNumber("1234567890");
        userProfileDTO.setAddress("123 Main St, Springfield");

        assertDoesNotThrow(() -> validateUserProfileDTO(userProfileDTO));
    }

    @Test
    void testFullNameBlank() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("");
        userProfileDTO.setEmail("johndoe@example.com");
        userProfileDTO.setPhoneNumber("1234567890");
        userProfileDTO.setAddress("123 Main St, Springfield");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateUserProfileDTO(userProfileDTO));
        assertEquals("Full name is required", exception.getMessage());
    }

    @Test
    void testFullNameTooShort() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("J");
        userProfileDTO.setEmail("johndoe@example.com");
        userProfileDTO.setPhoneNumber("1234567890");
        userProfileDTO.setAddress("123 Main St, Springfield");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateUserProfileDTO(userProfileDTO));
        assertEquals("Full name must be between 2 and 100 characters", exception.getMessage());
    }

    @Test
    void testInvalidEmail() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("John Doe");
        userProfileDTO.setEmail("invalid-email");
        userProfileDTO.setPhoneNumber("1234567890");
        userProfileDTO.setAddress("123 Main St, Springfield");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateUserProfileDTO(userProfileDTO));
        assertEquals("Please provide a valid email address", exception.getMessage());
    }

    @Test
    void testInvalidPhoneNumber() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("John Doe");
        userProfileDTO.setEmail("johndoe@example.com");
        userProfileDTO.setPhoneNumber("12345");
        userProfileDTO.setAddress("123 Main St, Springfield");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateUserProfileDTO(userProfileDTO));
        assertEquals("Please provide a valid 10-digit phone number", exception.getMessage());
    }

    @Test
    void testBlankAddress() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("John Doe");
        userProfileDTO.setEmail("johndoe@example.com");
        userProfileDTO.setPhoneNumber("1234567890");
        userProfileDTO.setAddress("");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateUserProfileDTO(userProfileDTO));
        assertEquals("Address is required", exception.getMessage());
    }

    @Test
    void testAddressTooShort() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("John Doe");
        userProfileDTO.setEmail("johndoe@example.com");
        userProfileDTO.setPhoneNumber("1234567890");
        userProfileDTO.setAddress("123");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateUserProfileDTO(userProfileDTO));
        assertEquals("Address must be between 5 and 200 characters", exception.getMessage());
    }

    @Test
    void testNullFields() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName(null);
        userProfileDTO.setEmail("Anything@gmail.com");
        userProfileDTO.setPhoneNumber("123456789");
        userProfileDTO.setAddress("1-22-333");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateUserProfileDTO(userProfileDTO));
        assertEquals("Full name is required", exception.getMessage());
    }

    private void validateUserProfileDTO(UserProfileDTO userProfileDTO) {
        if (userProfileDTO.getFullName() == null || userProfileDTO.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (userProfileDTO.getFullName().length() < 2 || userProfileDTO.getFullName().length() > 100) {
            throw new IllegalArgumentException("Full name must be between 2 and 100 characters");
        }
        if (userProfileDTO.getEmail() == null || !userProfileDTO.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Please provide a valid email address");
        }
        if (userProfileDTO.getPhoneNumber() == null || !userProfileDTO.getPhoneNumber().matches("\\d{10}")) {
            throw new IllegalArgumentException("Please provide a valid 10-digit phone number");
        }
        if (userProfileDTO.getAddress() == null || userProfileDTO.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (userProfileDTO.getAddress().length() < 5 || userProfileDTO.getAddress().length() > 200) {
            throw new IllegalArgumentException("Address must be between 5 and 200 characters");
        }
    }
}
