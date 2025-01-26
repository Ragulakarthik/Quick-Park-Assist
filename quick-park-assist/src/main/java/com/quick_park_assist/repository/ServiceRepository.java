package com.quick_park_assist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.quick_park_assist.entity.ServiceEntity;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByUserId(Long userId);
    Optional<ServiceEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsServiceEntityByNameIgnoreCase(String name);
}
