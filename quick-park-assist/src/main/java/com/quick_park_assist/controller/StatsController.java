package  com.quick_park_assist.controller;

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

    private  IStatsService statsService;

    @Autowired
    public StatsController(IStatsService statsService) {
        this.statsService = statsService;
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatsForUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Unauthorized
        }
        Map<String, Object> stats = statsService.getStatsForUser(userId);
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity(HttpSession session) {
        Long loggedInUser = (Long) session.getAttribute("userId");
        if (loggedInUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Or handle as needed
        }
        List<Map<String, Object>> recentActivity = statsService.getRecentActivity(loggedInUser,4);
        return ResponseEntity.ok(recentActivity);
    }
}

