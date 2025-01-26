package com.quick_park_assist.utilTest;

import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.util.PasswordMatchValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordMatchValidatorTest {

    private PasswordMatchValidator passwordMatchValidator;
    private Errors errors;

    @BeforeEach
    void setUp() {
        passwordMatchValidator = new PasswordMatchValidator();
        errors = new MapBindingResult(new java.util.HashMap<>(), "userRegistrationDTO");
    }

    @Test
    void testValidate_PasswordsMatch() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("validPassword123");
        dto.setConfirmPassword("validPassword123");

        // Act
        passwordMatchValidator.validate(dto, errors);

        // Assert
        assertEquals(0, errors.getErrorCount(), "There should be no errors when passwords match.");
    }

    @Test
    void testValidate_PasswordsDoNotMatch() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("validPassword123");
        dto.setConfirmPassword("differentPassword123");

        // Act
        passwordMatchValidator.validate(dto, errors);

        // Assert
        assertEquals(1, errors.getErrorCount(), "There should be one error when passwords do not match.");
        assertEquals("password.mismatch", errors.getFieldError("confirmPassword").getCode());
    }

    @Test
    void testValidate_EmptyPassword() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("");
        dto.setConfirmPassword("");

        // Act
        passwordMatchValidator.validate(dto, errors);

        // Assert
        assertEquals(0, errors.getErrorCount(), "There should be no errors when both passwords are empty.");
    }


    @Test
    void testValidate_PasswordOnlyProvided() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("validPassword123");
        dto.setConfirmPassword("");

        // Act
        passwordMatchValidator.validate(dto, errors);

        // Assert
        assertEquals(1, errors.getErrorCount(), "There should be one error when only the password is provided.");
        assertEquals("password.mismatch", errors.getFieldError("confirmPassword").getCode());
    }

    @Test
    void testValidate_ConfirmPasswordOnlyProvided() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("");
        dto.setConfirmPassword("validPassword123");

        // Act
        passwordMatchValidator.validate(dto, errors);

        // Assert
        assertEquals(1, errors.getErrorCount(), "There should be one error when only confirm password is provided.");
        assertEquals("password.mismatch", errors.getFieldError("confirmPassword").getCode());
    }
}
