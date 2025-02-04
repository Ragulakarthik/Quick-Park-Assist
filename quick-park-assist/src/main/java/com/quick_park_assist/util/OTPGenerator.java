package com.quick_park_assist.util;

import org.springframework.stereotype.Component;

@Component
public class OTPGenerator {
    public String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // Generates a 6-digit OTP
    }
}
