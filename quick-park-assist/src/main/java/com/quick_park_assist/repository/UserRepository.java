package com.quick_park_assist.repository;

import com.quick_park_assist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);  // Add this method
    Optional<User> findByFullName(String fullName);
    Optional<User> findById(Long id);
}