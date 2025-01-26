package com.quick_park_assist.controllerTest;

import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.serviceImpl.ServiceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceHandlerTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceHandler serviceHandler;

    private ServiceEntity testService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testService = new ServiceEntity();
        testService.setId(1L);
        testService.setName("Test Service");
        testService.setPrice(100.0);
        testService.setDescription("Test Description");
    }

    @Test
    public void testGetAllServices_ShouldReturnList() {
        List<ServiceEntity> expectedList = new ArrayList<>();
        expectedList.add(testService);
        when(serviceRepository.findAll()).thenReturn(expectedList);

        List<ServiceEntity> result = serviceHandler.getAllServices();

        assertEquals(expectedList, result);
        verify(serviceRepository).findAll();
    }

    @Test
    public void testSaveService_ShouldSaveSuccessfully() {
        serviceHandler.saveService(testService);
        verify(serviceRepository).save(testService);
    }
}