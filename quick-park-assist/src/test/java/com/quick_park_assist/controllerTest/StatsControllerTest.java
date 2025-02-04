package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.StatsController;
import com.quick_park_assist.service.IStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StatsControllerTest {

    private StatsController statsController;
    private IStatsService statsService;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        statsService = Mockito.mock(IStatsService.class);
        statsController = new StatsController(statsService);
        session = Mockito.mock(HttpSession.class);
    }

    // Test cases for getStatsForUser
    @Test
    void testGetStatsForUser_Unauthorized_NoUserId() {
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("userType")).thenReturn("VEHICLE_OWNER");

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_Unauthorized_NoUserType() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_Forbidden_InvalidUserType() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("ADMIN");

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_VehicleOwner_Success() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("VEHICLE_OWNER");
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("key", "value");
        when(statsService.getStatsForUser(1L)).thenReturn(mockStats);

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStats, response.getBody());
    }

    @Test
    void testGetStatsForUser_SpotOwner_Success() {
        when(session.getAttribute("userId")).thenReturn(2L);
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER");
        Map<String, Object> mockSpotOwnerStats = new HashMap<>();
        mockSpotOwnerStats.put("spot", "data");
        when(statsService.getSpotOwnerStats(2L)).thenReturn(mockSpotOwnerStats);

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSpotOwnerStats, response.getBody());
    }

    // Test cases for getRecentActivity
    @Test
    void testGetRecentActivity_Unauthorized_NoUserId() {
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<List<Map<String, Object>>> response = statsController.getRecentActivity(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetRecentActivity_Unauthorized_InvalidUserIdType() {
        when(session.getAttribute("userId")).thenReturn("notALong");

        ResponseEntity<List<Map<String, Object>>> response = statsController.getRecentActivity(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetRecentActivity_Success() {
        when(session.getAttribute("userId")).thenReturn(3L);
        List<Map<String, Object>> mockRecentActivity = new ArrayList<>();
        Map<String, Object> activity = new HashMap<>();
        activity.put("activity", "data");
        mockRecentActivity.add(activity);

        when(statsService.getRecentActivity(3L, 4)).thenReturn(mockRecentActivity);

        ResponseEntity<List<Map<String, Object>>> response = statsController.getRecentActivity(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecentActivity, response.getBody());
    }
    // Additional test cases for getStatsForUser
    @Test
    void testGetStatsForUser_NullSession() {

        when(session.getAttribute("userId")).thenReturn(null);
        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_BothUserIdAndUserTypeNull() {
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("userType")).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_EmptyUserType() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("");

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_UnhandledUserType() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("RANDOM_TYPE");

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_VehicleOwner_EmptyStats() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("VEHICLE_OWNER");
        when(statsService.getStatsForUser(1L)).thenReturn(new HashMap<>());

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new HashMap<>(), response.getBody());
    }

    @Test
    void testGetStatsForUser_VehicleOwner_NullStats() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("VEHICLE_OWNER");
        when(statsService.getStatsForUser(1L)).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetStatsForUser_SpotOwner_NegativeUserId() {
        when(session.getAttribute("userId")).thenReturn(-1L);
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER");
        when(statsService.getSpotOwnerStats(-1L)).thenReturn(new HashMap<>());

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new HashMap<>(), response.getBody());
    }

    @Test
    void testGetStatsForUser_SpotOwner_NullStats() {
        when(session.getAttribute("userId")).thenReturn(2L);
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER");
        when(statsService.getSpotOwnerStats(2L)).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = statsController.getStatsForUser(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    // Additional test cases for getRecentActivity
    @Test
    void testGetRecentActivity_NullSession() {
        ResponseEntity<List<Map<String, Object>>> response = statsController.getRecentActivity(session);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetRecentActivity_EmptyRecentActivity() {
        when(session.getAttribute("userId")).thenReturn(3L);
        when(statsService.getRecentActivity(3L, 4)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Map<String, Object>>> response = statsController.getRecentActivity(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void testGetRecentActivity_NullRecentActivity() {
        when(session.getAttribute("userId")).thenReturn(3L);
        when(statsService.getRecentActivity(3L, 4)).thenReturn(null);

        ResponseEntity<List<Map<String, Object>>> response = statsController.getRecentActivity(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetRecentActivity_NegativeUserId() {
        when(session.getAttribute("userId")).thenReturn(-1L);
        when(statsService.getRecentActivity(-1L, 4)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Map<String, Object>>> response = statsController.getRecentActivity(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

}
