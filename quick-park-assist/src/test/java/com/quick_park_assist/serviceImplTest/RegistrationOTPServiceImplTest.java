package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.entity.OTP;
import com.quick_park_assist.repository.OTPRepository;
import com.quick_park_assist.serviceImpl.RegistrationOTPServiceImpl;
import com.quick_park_assist.util.OTPGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrationOTPServiceImplTest {

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private OTPGenerator otpGenerator;

    @InjectMocks
    private RegistrationOTPServiceImpl registrationOTPService;

    private OTP testOTP;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestData();
    }

    void setupTestData() {
        testOTP = new OTP();
        testOTP.setUserId(-1L);
        testOTP.setOtpCode("123456");
        testOTP.setExpirationTime(LocalDateTime.now().plusMinutes(10));
    }

    @Test
    void testSendRegistrationOTP() {
        when(otpGenerator.generateOTP()).thenReturn("123456");
        when(otpRepository.save(any(OTP.class))).thenReturn(testOTP);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String result = registrationOTPService.sendRegistrationOTP("test@example.com");

        assertEquals("OTP sent to your email", result);
        verify(otpRepository).save(any(OTP.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testVerifyRegistrationOTP_Success() {
        when(otpRepository.findByUserIdAndOtpCode(-1L, "123456"))
                .thenReturn(Optional.of(testOTP));

        boolean result = registrationOTPService.verifyRegistrationOTP("test@example.com", "123456");

        assertTrue(result);
        verify(otpRepository).delete(testOTP);
    }

    @Test
    void testVerifyRegistrationOTP_InvalidOTP() {
        when(otpRepository.findByUserIdAndOtpCode(-1L, "654321"))
                .thenReturn(Optional.empty());

        boolean result = registrationOTPService.verifyRegistrationOTP("test@example.com", "654321");

        assertFalse(result);
        verify(otpRepository, never()).delete(any());
    }

    @Test
    void testVerifyRegistrationOTP_ExpiredOTP() {
        testOTP.setExpirationTime(LocalDateTime.now().minusMinutes(1));
        when(otpRepository.findByUserIdAndOtpCode(-1L, "123456"))
                .thenReturn(Optional.of(testOTP));

        boolean result = registrationOTPService.verifyRegistrationOTP("test@example.com", "123456");

        assertFalse(result);
        verify(otpRepository, never()).delete(any());
    }

    @Test
    void testSendRegistrationOTP_EmailValidation() {
        when(otpGenerator.generateOTP()).thenReturn("123456");
        when(otpRepository.save(any(OTP.class))).thenReturn(testOTP);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String result = registrationOTPService.sendRegistrationOTP("invalid.email");

        assertEquals("OTP sent to your email", result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendRegistrationOTP_MailSenderException() {
        when(otpGenerator.generateOTP()).thenReturn("123456");
        when(otpRepository.save(any(OTP.class))).thenReturn(testOTP);
        doThrow(new RuntimeException("Mail server error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () ->
                registrationOTPService.sendRegistrationOTP("test@example.com"));
    }
}