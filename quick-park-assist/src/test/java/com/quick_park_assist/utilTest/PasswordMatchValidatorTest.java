package com.quick_park_assist.utilTest;

import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.util.PasswordMatchValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

class PasswordMatchValidatorTest {

    private PasswordMatchValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchValidator();
    }

    @Test
    void testSupportsReturnsTrueForUserRegistrationDTO() {
        assertTrue(validator.supports(UserRegistrationDTO.class),
                "Validator should support UserRegistrationDTO class");
    }

    @Test
    void testSupportsReturnsFalseForOtherClasses() {
        assertFalse(validator.supports(Object.class),
                "Validator should not support other classes");
    }

    @Test
    void testValidateWhenPasswordsMatch() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        Errors errors = new BeanPropertyBindingResult(dto, "userRegistrationDTO");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(), "No validation errors should be present when passwords match");
    }

    @Test
    void testValidateWhenPasswordsDoNotMatch() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("password123");
        dto.setConfirmPassword("differentPassword");

        Errors errors = new BeanPropertyBindingResult(dto, "userRegistrationDTO");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Validation errors should be present when passwords do not match");
        assertEquals(1, errors.getErrorCount(), "There should be exactly one validation error");
        assertEquals("password.mismatch", errors.getFieldError("confirmPassword").getCode(),
                "Error code should be 'password.mismatch'");
        assertEquals("Password and confirm password do not match", errors.getFieldError("confirmPassword").getDefaultMessage(),
                "Error message should be 'Password and confirm password do not match'");
    }

    

    @Test
    void testValidateWhenPasswordsAreEmptyStrings() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("");
        dto.setConfirmPassword("");

        Errors errors = new BeanPropertyBindingResult(dto, "userRegistrationDTO");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(), "No validation errors should be present when passwords are empty strings");
    }
}
