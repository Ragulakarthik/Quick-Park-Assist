package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.serviceImpl.ModifyOwnerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModifyOwnerServiceImplTest {

    @InjectMocks
    private ModifyOwnerServiceImpl modifyOwnerService;

    @Mock
    private ServiceRepository serviceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOwnerServices() {
        Long userId = 1L;
        List<ServiceEntity> mockServices = List.of(new ServiceEntity(), new ServiceEntity());

        when(serviceRepository.findByUserId(userId)).thenReturn(mockServices);

        List<ServiceEntity> services = modifyOwnerService.getOwnerServices(userId);

        assertNotNull(services);
        assertEquals(2, services.size());
        verify(serviceRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testUpdateServiceDetails_ServiceExists() {
        Long bookingId = 1L;
        String name = "New Service Name";
        String description = "Updated Description";
        Double price = 99.99;

        ServiceEntity mockService = new ServiceEntity();
        mockService.setId(bookingId);

        when(serviceRepository.findById(bookingId)).thenReturn(Optional.of(mockService));

        boolean result = modifyOwnerService.updateServiceDetails(bookingId, name, description, price);

        assertTrue(result);
        assertEquals(name, mockService.getName());
        assertEquals(description, mockService.getDescription());
        assertEquals(price, mockService.getPrice());
        verify(serviceRepository, times(1)).findById(bookingId);
        verify(serviceRepository, times(1)).save(mockService);
    }

    @Test
    void testUpdateServiceDetails_ServiceDoesNotExist() {
        Long bookingId = 1L;

        when(serviceRepository.findById(bookingId)).thenReturn(Optional.empty());

        boolean result = modifyOwnerService.updateServiceDetails(bookingId, "name", "description", 100.0);

        assertFalse(result);
        verify(serviceRepository, times(1)).findById(bookingId);
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }

    @Test
    void testRemoveService_ServiceExists() {
        Long id = 1L;
        Long userId = 2L;
        ServiceEntity mockService = new ServiceEntity();

        when(serviceRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(mockService));

        boolean result = modifyOwnerService.removeService(id, userId);

        assertTrue(result);
        verify(serviceRepository, times(1)).findByIdAndUserId(id, userId);
        verify(serviceRepository, times(1)).deleteById(id);
    }

    @Test
    void testRemoveService_ServiceDoesNotExist() {
        Long id = 1L;
        Long userId = 2L;

        when(serviceRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        boolean result = modifyOwnerService.removeService(id, userId);

        assertFalse(result);
        verify(serviceRepository, times(1)).findByIdAndUserId(id, userId);
        verify(serviceRepository, never()).deleteById(anyLong());
    }
}
