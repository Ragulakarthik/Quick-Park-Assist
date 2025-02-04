package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.OTP;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.OTPRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import com.quick_park_assist.util.OTPGenerator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger log = LoggerFactory.getLogger(OTPServiceImpl.class);

    @Override
    @Transactional
    public String sendOTP(User user) {
        // Generate and save OTP
        String otpCode = otpGenerator.generateOTP();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10); // OTP valid for 10 minutes

// use this line of code only if u want user to use the latest otp sent
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

        return "OTP sent to your email";
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
//        // OTP verified, clean up the repository to avoid reuse
        otpRepository.deleteByUserId(user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(@RequestParam("email") String email, String newPassword) {
        log.info("From Service Impl your Email is = {}",  email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void sendPasswordChangeEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Security Alert: Password Changed");
        message.setText("SECURITY ALERT: Your password was just changed. If you did not make this change, please contact support immediately and secure your account.");
        mailSender.send(message);
        log.info("Password change Message sent");
    }



    @Override
    public void sendAccountStatusEmail(String email, boolean isDeactivation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);

        if (isDeactivation) {
            message.setSubject("Account Deactivation Notice");
            String messageText = String.format(
                    "Your account has been deactivated. %n"
                            + "You can reactivate it anytime by logging in with your credentials %n"
                            + "and verifying your email address.%n%n"
                            + "If you did not request this action, please contact support immediately."
            );

            message.setText(messageText);

        } else {
            message.setSubject("Account Reactivation Confirmation");
            String messageText = String.format(
                    "Your account has been successfully reactivated. %n"
                            + "You now have full access to all services.%n%n"
                            + "Welcome back!"
            );

            message.setText(messageText);
        }

        mailSender.send(message);
    }

    @Override
    public void sendReactivationOTP(User user) {
        String otpCode = otpGenerator.generateOTP();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);

        OTP otp = new OTP();
        otp.setUserId(user.getId());
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(expirationTime);
        otpRepository.save(otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification OTP");
        message.setText("Your OTP for account reactivation is: " + otpCode);
        mailSender.send(message);
    }

    @Override
    @Transactional
    public boolean verifyReactivationOTP(String email, String otpCode) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        Optional<OTP> otpOpt = otpRepository.findByUserIdAndOtpCode(user.getId(), otpCode);

        if (otpOpt.isEmpty() || otpOpt.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        otpRepository.deleteByUserId(user.getId());
        return true;
    }


}
