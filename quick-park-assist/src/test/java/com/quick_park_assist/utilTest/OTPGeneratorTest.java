package com.quick_park_assist.utilTest;

import com.quick_park_assist.util.OTPGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OTPGeneratorTest {

    private OTPGenerator otpGenerator;

    @BeforeEach
    void setUp() {
        otpGenerator = new OTPGenerator();
    }

    @Test
    void testGenerateOTP_Returns6DigitOTP() {
        // Act
        String otp = otpGenerator.generateOTP();

        // Assert
        assertEquals(6, otp.length(), "OTP should be 6 digits long");
        assertTrue(otp.matches("\\d{6}"), "OTP should consist of 6 digits only");
    }



    @Test
    void testGenerateOTP_RangeCheck() {
        // Act
        String otp = otpGenerator.generateOTP();
        int otpValue = Integer.parseInt(otp);

        // Assert
        assertTrue(otpValue >= 100000 && otpValue <= 999999, "OTP should be in the range 100000-999999");
    }
}
