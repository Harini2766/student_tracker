package controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.DailyCodingDTO;
import entity.User;
import repository.UserRepository;
import service.CodingLogService;

@RestController
@RequestMapping("/api") // Base mapping for both student and staff
// *** REMOVED: @CrossOrigin(origins = "http://localhost:9091") to prevent conflicts ***
public class CodingLogController {

    @Autowired
    private CodingLogService codingLogService;

    @Autowired
    private UserRepository userRepository;

    // --- STUDENT ENDPOINTS (Keep these) ---
    @GetMapping("/student/daily-coding")
    public ResponseEntity<DailyCodingDTO> getStats(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElseThrow();
            return ResponseEntity.ok(codingLogService.getStudentStats(user.getUserId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/student/daily-coding")
    public ResponseEntity<?> logCoding(@RequestBody Map<String, Integer> payload, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElseThrow();

            Integer count = payload.get("solved");
            if(count == null || count < 1) {
				throw new RuntimeException("Invalid count");
			}

            codingLogService.logDailyCoding(user.getUserId(), count);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // *** REMOVED: STAFF ENDPOINT MOVED TO StaffController ***
}