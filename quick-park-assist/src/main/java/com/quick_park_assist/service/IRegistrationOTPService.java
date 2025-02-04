package com.quick_park_assist.service;

public interface IRegistrationOTPService {
    String sendRegistrationOTP(String email);
    boolean verifyRegistrationOTP(String email, String otpCode);
}