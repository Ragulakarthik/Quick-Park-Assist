package com.quick_park_assist.service;

import com.quick_park_assist.dto.UserProfileDTO;
import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.serviceImpl.UserServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface IUserService {
    User registerUser(UserRegistrationDTO registrationDTO) throws UserServiceImpl.PasswordHashingException;
    boolean isEmailTaken(String email);
    boolean isPhoneNumberTaken(String phoneNumber);
    User authenticateUser(String email, String password) throws UserServiceImpl.PasswordHashingException;  // Changed return type to User

    User getUserById(Long id);
    void updateProfile(Long userId, UserProfileDTO profileDTO);

    void deactivateAccount(Long userId);
    void deleteAccount(Long userId);
    boolean isAccountActive(String email);
    void reactivateAccount(String email);


}