package com.quick_park_assist.repository;

import com.quick_park_assist.entity.AddonService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddonRepository extends JpaRepository<AddonService, Long> {
    List<AddonService> findByUserId(Long userId);

}
