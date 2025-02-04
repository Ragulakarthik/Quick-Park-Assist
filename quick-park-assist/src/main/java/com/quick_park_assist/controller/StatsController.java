package com.quick_park_assist.controller;

import com.quick_park_assist.service.IStatsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final  IStatsService statsService;

    @Autowired
    public StatsController(IStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatsForUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (userId == null || userType == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Unauthorized
        }

        if ("VEHICLE_OWNER".equals(userType)) {
            Map<String, Object> stats = statsService.getStatsForUser(userId);
            return ResponseEntity.ok(stats);
        } else if ("SPOT_OWNER".equals(userType)) {
            Map<String, Object> spotOwnerStats = statsService.getSpotOwnerStats(userId);
            return ResponseEntity.ok(spotOwnerStats);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Forbidden for other user types
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null || !(userIdObj instanceof Long)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Unauthorized
        }

        Long loggedInUser = (Long) userIdObj;
        List<Map<String, Object>> recentActivity = statsService.getRecentActivity(loggedInUser, 4);
        return ResponseEntity.ok(recentActivity);
    }
}
