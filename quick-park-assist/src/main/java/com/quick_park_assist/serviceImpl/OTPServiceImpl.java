package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.OTP;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.OTPRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import com.quick_park_assist.util.OTPGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPServiceImpl implements IOTPService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OTPGenerator otpGenerator;

    @Override
    @Transactional
    public String sendOTP(User user) {
        // Generate and save OTP
        String otpCode = otpGenerator.generateOTP();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10); // OTP valid for 10 minutes

        otpRepository.deleteByUserId(user.getId()); // Remove old OTPs
        OTP otp = new OTP();
        otp.setUserId(user.getId());
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(expirationTime);
        otpRepository.save(otp);

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP is: " + otpCode);
        mailSender.send(message);

        return "OTP Successfully Sent";
    }

    @Override
    @Transactional
    public boolean verifyOTP(@RequestParam("email") String email, String otpCode) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        Optional<OTP> otpOpt = otpRepository.findByUserIdAndOtpCode(user.getId(), otpCode);
        if (otpOpt.isEmpty()) {
            return false;
        }

        OTP otp = otpOpt.get();
        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        // OTP verified, clean up the repository to avoid reuse
        otpRepository.deleteByUserId(user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(@RequestParam("email") String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Hash the new password (use a secure hashing mechanism like BCrypt)
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }


}

