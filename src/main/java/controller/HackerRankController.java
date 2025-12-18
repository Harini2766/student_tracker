package controller;

import java.security.Principal; // Import Principal
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.StudentProfile;
import entity.User; // Import User entity
import repository.StudentProfileRepository;
import repository.UserRepository; // Import UserRepository
import service.HackerRankService; // Import the Service

@RestController
@RequestMapping("/api/data/hackerrank")
public class HackerRankController {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    // *** FIX 1: INJECT DEPENDENCIES ***
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HackerRankService hackerRankService;

    // --- Helper to get User ID ---
    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    // 1. Student API for Stats (formerly /api/hackerrank/stats)
    // *** FIX 2: IMPLEMENTATION TO CALL HACKERRANK SERVICE ***
    @GetMapping("/stats")
    public ResponseEntity<?> getHackerRankStats(Principal principal) {
        try {
            User user = getUser(principal);

            // Call the service to fetch the DTO for the logged-in user
            return ResponseEntity.ok(hackerRankService.getUserStats(user.getUserId()));

        } catch (RuntimeException e) {
            // If link is missing or API fetch failed, return an empty DTO or error message
            System.err.println("HackerRank fetch failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error fetching HackerRank data: " + e.getMessage());
        }
    }

    // 2. Staff API for the "Skill Rankings" Table (now /api/data/hackerrank/leaderboard)
    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getHackerRankLeaderboard() {
        List<StudentProfile> profiles = studentProfileRepository.findAll();

        profiles.sort((p1, p2) -> {
            int b1 = (p1.getHackerrankBadges() == null) ? 0 : p1.getHackerrankBadges();
            int b2 = (p2.getHackerrankBadges() == null) ? 0 : p2.getHackerrankBadges();
            return Integer.compare(b2, b1); // Descending sort
        });

        List<Map<String, Object>> response = new ArrayList<>();
        int rank = 1;

        for (StudentProfile p : profiles) {
            if (p.getUser() == null) {
				continue;
			}

            int badges = (p.getHackerrankBadges() == null) ? 0 : p.getHackerrankBadges();
            int score = badges * 10;

            Map<String, Object> row = new HashMap<>();
            row.put("rank", rank++);
            row.put("name", p.getUser().getFullName());
            row.put("score", score);
            row.put("badges", badges);
            row.put("star_rating", calculateStars(badges));

            response.add(row);
        }
        return ResponseEntity.ok(response);
    }

    // 3. API for Certificates (now /api/data/hackerrank/certificates)
    @GetMapping("/certificates")
    public ResponseEntity<List<Map<String, Object>>> getCertificates() {
        List<StudentProfile> profiles = studentProfileRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (StudentProfile p : profiles) {
            int certCount = (p.getCertificatesCount() == null) ? 0 : p.getCertificatesCount();

            if (certCount > 0 && p.getUser() != null) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentName", p.getUser().getFullName());
                row.put("certificatesEarned", certCount + " Certificates Earned");
                row.put("status", "Verified");

                response.add(row);
            }
        }
        return ResponseEntity.ok(response);
    }

    private int calculateStars(int badges) {
        if (badges >= 10) {
			return 5;
		}
        if (badges >= 7) {
			return 4;
		}
        if (badges >= 4) {
			return 3;
		}
        if (badges >= 1) {
			return 2;
		}
        return 1;
    }
}