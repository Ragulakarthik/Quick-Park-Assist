package com.quick_park_assist.serviceImpl;


import com.quick_park_assist.entity.ServiceEntity;

import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.service.IModifyOwnerService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.Optional;

@Service
public class ModifyOwnerServiceImpl implements IModifyOwnerService {
    @Autowired
    ServiceRepository serviceRepository;

    @Override
    public List<ServiceEntity> getOwnerServices(Long userId) {
        return serviceRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public boolean updateServiceDetails(@PathVariable Long bookingId, @RequestBody String name, @RequestBody String description, @RequestBody Double price) {
        Optional<ServiceEntity> service = serviceRepository.findById(bookingId);
        if(service.isPresent()){
            ServiceEntity serviceEntity =  service.get();
            serviceEntity.setDescription(description);
            serviceEntity.setName(name);
            serviceEntity.setPrice(price);
            serviceRepository.save(serviceEntity);
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    public boolean removeService(Long Id,Long userId){
        Optional<ServiceEntity> service = serviceRepository.findByIdAndUserId(Id, userId);
        if(service.isPresent()){
            serviceRepository.deleteById(Id);
            return true;
        }
        return false;
    }

}
