package com.quick_park_assist.service;

import com.quick_park_assist.entity.ServiceEntity;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public interface IModifyOwnerService {
    List<ServiceEntity> getOwnerServices(Long userId);
    boolean updateServiceDetails(Long id, String name, String description, Double price);
    boolean removeService(Long id,Long userId);
}
