package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.entity.OTP;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.OTPRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.serviceImpl.OTPServiceImpl;
import com.quick_park_assist.util.OTPGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OTPServiceImplTest {

    @InjectMocks
    private OTPServiceImpl otpService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private OTPGenerator otpGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOTP() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        String generatedOTP = "123456";

        when(otpGenerator.generateOTP()).thenReturn(generatedOTP);

        // Act
        String result = otpService.sendOTP(user);

        // Assert
        assertEquals("OTP sent to your email", result);

        // Verify OTP saved in repository
        ArgumentCaptor<OTP> otpCaptor = ArgumentCaptor.forClass(OTP.class);
        verify(otpRepository, times(1)).deleteByUserId(user.getId());
        verify(otpRepository, times(1)).save(otpCaptor.capture());
        OTP savedOTP = otpCaptor.getValue();
        assertEquals(user.getId(), savedOTP.getUserId());
        assertEquals(generatedOTP, savedOTP.getOtpCode());
        assertTrue(savedOTP.getExpirationTime().isAfter(LocalDateTime.now()));

        // Verify email sent
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Password Reset OTP", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(generatedOTP));
    }

    @Test
    void testVerifyOTP_Success() {
        // Arrange
        String email = "test@example.com";
        String otpCode = "123456";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        OTP otp = new OTP();
        otp.setUserId(1L);
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserIdAndOtpCode(user.getId(), otpCode)).thenReturn(Optional.of(otp));

        // Act
        boolean result = otpService.verifyOTP(email, otpCode);

        // Assert
        assertTrue(result);
        verify(otpRepository, times(1)).deleteByUserId(user.getId());
    }

    @Test
    void testVerifyOTP_InvalidUser() {
        // Arrange
        String email = "invalid@example.com";
        String otpCode = "123456";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = otpService.verifyOTP(email, otpCode);

        // Assert
        assertFalse(result);
        verify(otpRepository, never()).findByUserIdAndOtpCode(anyLong(), anyString());
    }

    @Test
    void testVerifyOTP_ExpiredOTP() {
        // Arrange
        String email = "test@example.com";
        String otpCode = "123456";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        OTP otp = new OTP();
        otp.setUserId(1L);
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(LocalDateTime.now().minusMinutes(1)); // Expired OTP

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserIdAndOtpCode(user.getId(), otpCode)).thenReturn(Optional.of(otp));

        // Act
        boolean result = otpService.verifyOTP(email, otpCode);

        // Assert
        assertFalse(result);
        verify(otpRepository, never()).deleteByUserId(user.getId());
    }

    @Test
    void testResetPassword_Success() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword123";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean result = otpService.resetPassword(email, newPassword);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).save(user);
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void testResetPassword_InvalidUser() {
        // Arrange
        String email = "invalid@example.com";
        String newPassword = "newPassword123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = otpService.resetPassword(email, newPassword);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
    // Test Case 4.1: Send Password Change Email
    @Test
    void testSendPasswordChangeEmail() {
        // Arrange
        String email = "test@example.com";
        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        doNothing().when(mailSender).send(mailCaptor.capture());

        // Act
        otpService.sendPasswordChangeEmail(email);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
        SimpleMailMessage message = mailCaptor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Security Alert: Password Changed", message.getSubject());
    }
    @Test
    void testResetPasswordSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("oldPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean result = otpService.resetPassword(email, newPassword);

        // Assert
        assertTrue(result);
        assertEquals(newPassword, user.getPassword());
        verify(userRepository).save(user);
    }
    // Test cases for sendAccountStatusEmail
    @Test
    void testSendAccountStatusEmail_Deactivation() {
        // Arrange
        String email = "user@example.com";
        boolean isDeactivation = true;

        // Act
        otpService.sendAccountStatusEmail(email, isDeactivation);

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Account Deactivation Notice", message.getSubject());
        assertTrue(message.getText().contains("Your account has been deactivated."));
    }

    @Test
    void testSendAccountStatusEmail_Reactivation() {
        // Arrange
        String email = "user@example.com";
        boolean isDeactivation = false;

        // Act
        otpService.sendAccountStatusEmail(email, isDeactivation);

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Account Reactivation Confirmation", message.getSubject());
        assertTrue(message.getText().contains("Your account has been successfully reactivated."));
    }

    // Test cases for sendReactivationOTP
    @Test
    void testSendReactivationOTP() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        String generatedOtp = "123456";
        when(otpGenerator.generateOTP()).thenReturn(generatedOtp);

        // Act
        otpService.sendReactivationOTP(user);

        // Assert
        ArgumentCaptor<OTP> otpCaptor = ArgumentCaptor.forClass(OTP.class);
        verify(otpRepository, times(1)).save(otpCaptor.capture());

        OTP savedOtp = otpCaptor.getValue();
        assertEquals(user.getId(), savedOtp.getUserId());
        assertEquals(generatedOtp, savedOtp.getOtpCode());
        assertTrue(savedOtp.getExpirationTime().isAfter(LocalDateTime.now()));

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(user.getEmail(), message.getTo()[0]);
        assertEquals("Email Verification OTP", message.getSubject());
        assertTrue(message.getText().contains("Your OTP for account reactivation is: " + generatedOtp));
    }

    // Test cases for verifyReactivationOTP
    @Test
    void testVerifyReactivationOTP_ValidOtp() {
        // Arrange
        String email = "user@example.com";
        String otpCode = "123456";

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        OTP otp = new OTP();
        otp.setUserId(user.getId());
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        when(otpRepository.findByUserIdAndOtpCode(user.getId(), otpCode)).thenReturn(Optional.of(otp));

        // Act
        boolean result = otpService.verifyReactivationOTP(email, otpCode);

        // Assert
        assertTrue(result);
        verify(otpRepository, times(1)).deleteByUserId(user.getId());
    }

    @Test
    void testVerifyReactivationOTP_InvalidOtp() {
        // Arrange
        String email = "user@example.com";
        String otpCode = "123456";

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserIdAndOtpCode(user.getId(), otpCode)).thenReturn(Optional.empty());

        // Act
        boolean result = otpService.verifyReactivationOTP(email, otpCode);

        // Assert
        assertFalse(result);
        verify(otpRepository, times(0)).deleteByUserId(user.getId());
    }

    @Test
    void testVerifyReactivationOTP_ExpiredOtp() {
        // Arrange
        String email = "user@example.com";
        String otpCode = "123456";

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        OTP otp = new OTP();
        otp.setUserId(user.getId());
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(LocalDateTime.now().minusMinutes(1)); // Expired OTP
        when(otpRepository.findByUserIdAndOtpCode(user.getId(), otpCode)).thenReturn(Optional.of(otp));

        // Act
        boolean result = otpService.verifyReactivationOTP(email, otpCode);

        // Assert
        assertFalse(result);
        verify(otpRepository, times(0)).deleteByUserId(user.getId());
    }

    @Test
    void testVerifyReactivationOTP_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String otpCode = "123456";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = otpService.verifyReactivationOTP(email, otpCode);

        // Assert
        assertFalse(result);
        verify(otpRepository, times(0)).findByUserIdAndOtpCode(anyLong(), anyString());
    }
}

