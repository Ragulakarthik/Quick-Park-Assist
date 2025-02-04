package com.quick_park_assist.repository;

import com.quick_park_assist.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
  Optional<OTP> findByUserIdAndOtpCode(Long userId, String otpCode);
  void deleteByUserId(Long userId);
}