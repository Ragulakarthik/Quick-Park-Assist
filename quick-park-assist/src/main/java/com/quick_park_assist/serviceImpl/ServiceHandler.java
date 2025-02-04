package com.quick_park_assist.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.repository.ServiceRepository;

import java.util.List;

@Service
public class ServiceHandler{

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public void saveService(ServiceEntity service) {
        serviceRepository.save(service);
    }
}