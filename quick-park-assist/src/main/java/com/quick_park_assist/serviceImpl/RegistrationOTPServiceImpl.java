package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.OTP;
import com.quick_park_assist.repository.OTPRepository;
import com.quick_park_assist.service.IRegistrationOTPService;
import com.quick_park_assist.util.OTPGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegistrationOTPServiceImpl implements IRegistrationOTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OTPGenerator otpGenerator;

    @Override
    @Transactional
    public String sendRegistrationOTP(String email) {
        String otpCode = otpGenerator.generateOTP();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);

        OTP otp = new OTP();
        otp.setUserId(-1L); // Special ID for registration OTPs
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(expirationTime);
        otpRepository.save(otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification OTP");
        message.setText("Your OTP for email verification is: " + otpCode);
        mailSender.send(message);

        return "OTP sent to your email";
    }

    @Override
    @Transactional
    public boolean verifyRegistrationOTP(String email, String otpCode) {
        Optional<OTP> otpOpt = otpRepository.findByUserIdAndOtpCode(-1L, otpCode);

        if (otpOpt.isPresent()) {
            OTP otp = otpOpt.get();
            if (otp.getExpirationTime().isAfter(LocalDateTime.now())) {
                otpRepository.delete(otp);
                return true;
            }
        }
        return false;
    }
}