package controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.LeetCodeDTO;
import entity.User;
import repository.UserRepository;
import service.LeetCodeService;

@RestController
@RequestMapping("/api/leetcode")
public class LeetCodeController {

    @Autowired
    private LeetCodeService leetCodeService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/leetcode/stats
     * Fetches real stats for the LOGGED-IN user.
     */
    @GetMapping("/stats")
    public ResponseEntity<LeetCodeDTO> getStudentStats(Principal principal) {
        try {
            // 1. Get Logged-in User
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Fetch Stats via Service (returns the DTO now)
            LeetCodeDTO stats = leetCodeService.getStatsDTO(user.getUserId());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/leetcode/stats/{studentId}
     * Staff view: Fetches stats for a specific student ID.
     */
    @GetMapping("/stats/{studentId}")
    public ResponseEntity<LeetCodeDTO> getStudentStatsForStaff(@PathVariable Long studentId) {
        try {
            // Staff is authorized to view any ID
            LeetCodeDTO stats = leetCodeService.getStatsDTO(studentId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/leetcode/refresh
     * Triggers a refresh for the logged-in user.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshData(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // *** FIX APPLIED HERE: Using the correct method name ***
            leetCodeService.refreshLeetCodeData(user.getUserId());
            return ResponseEntity.ok("LeetCode data refresh initiated.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to refresh data.");
        }
    }
}