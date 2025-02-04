package com.quick_park_assist.entityTest;


import com.quick_park_assist.entity.OTP;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OTPEntityTest {

    @Test
    void testIdGetterAndSetter() {
        OTP otp = new OTP();
        otp.setId(1L);

        assertThat(otp.getId()).isEqualTo(1L);
    }

    @Test
    void testUserIdGetterAndSetter() {
        OTP otp = new OTP();
        otp.setUserId(1001L);

        assertThat(otp.getUserId()).isEqualTo(1001L);
    }

    @Test
    void testOtpCodeGetterAndSetter() {
        OTP otp = new OTP();
        otp.setOtpCode("123456");

        assertThat(otp.getOtpCode()).isEqualTo("123456");
    }

    @Test
    void testExpirationTimeGetterAndSetter() {
        OTP otp = new OTP();
        LocalDateTime expirationTime = LocalDateTime.now();
        otp.setExpirationTime(expirationTime);

        assertThat(otp.getExpirationTime()).isEqualTo(expirationTime);
    }

    @Test
    void testFullEntity() {
        // Create and set all fields
        OTP otp = new OTP();
        otp.setId(1L);
        otp.setUserId(1001L);
        otp.setOtpCode("123456");
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        otp.setExpirationTime(expirationTime);

        // Assert all fields
        assertThat(otp.getId()).isEqualTo(1L);
        assertThat(otp.getUserId()).isEqualTo(1001L);
        assertThat(otp.getOtpCode()).isEqualTo("123456");
        assertThat(otp.getExpirationTime()).isEqualTo(expirationTime);
    }
}

