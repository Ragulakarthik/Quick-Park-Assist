package com.quick_park_assist.service;

import com.quick_park_assist.entity.User;
import org.springframework.web.bind.annotation.RequestParam;

public interface IOTPService {
     String sendOTP(User user);
     boolean verifyOTP(@RequestParam("email") String email, String otpCode);
     boolean resetPassword(@RequestParam("email") String email, String newPassword) ;
}