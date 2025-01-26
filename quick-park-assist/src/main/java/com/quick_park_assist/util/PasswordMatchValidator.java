package com.quick_park_assist.util;

import com.quick_park_assist.dto.UserRegistrationDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordMatchValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationDTO dto = (UserRegistrationDTO) target;

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch",
                    "Password and confirm password do not match");
        }
    }

}