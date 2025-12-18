package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal; // Ensures Principal is clearly imported
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import dto.CodingLogsResponseDTO;
import dto.GitHubAnalyticsDTO;
import dto.GitHubDTO;
import dto.HackerRankTrackerDTO;
import dto.LeetCodeTrackerDTO;
import dto.ResumeReviewDTO;
import dto.StaffSettingsDTO;// <--- MAKE SURE THIS IS PRESENT
import dto.DashboardDTO;

import dto.DepartmentGrowthDTO;
import dto.StudentReportDTO;
import dto.StudentMasterDTO;

import entity.User; // <--- You will need this, if not already present
import java.security.Principal;


import dto.StudentOverviewDTO;
import entity.HackerRankStats;
import entity.LeetCodeStats;
import entity.Resume;
import repository.HackerRankStatsRepository;
import repository.LeetCodeStatsRepository;
import service.CodingLogService;
import service.GitHubService;
import service.ResumeService;
import service.StaffService;
import service.StudentService;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private LeetCodeStatsRepository leetCodeStatsRepository;

    @Autowired
    private HackerRankStatsRepository hackerRankStatsRepository;

    @Autowired
    private CodingLogService codingLogService;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ResumeService resumeService;
    
    
 // --- 0. STAFF DASHBOARD ENDPOINT (NEW) ---
    /**
     * STAFF: Fetches all aggregated class metrics for the main dashboard view.
     */
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<DashboardDTO> getStaffDashboardMetrics() {
        try {
            DashboardDTO metrics = staffService.getStaffDashboardMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            System.err.println("Error fetching Staff Dashboard metrics: " + e.getMessage());
            // Return 500 status to inform the frontend of the failure
            return ResponseEntity.status(500).body(null); 
        }
    }

    // --- 1. LEETCODE TRACKER ENDPOINT (Existing) ---
    @GetMapping("/leetcode")
    public ResponseEntity<List<LeetCodeTrackerDTO>> getLeetCodeLeaderboard() {
        List<StudentOverviewDTO> students = staffService.getAllStudentsOverview();
        List<LeetCodeTrackerDTO> leaderboard = new ArrayList<>();

        for (StudentOverviewDTO student : students) {
            LeetCodeTrackerDTO dto = new LeetCodeTrackerDTO();
            dto.setId(student.getUserId());
            dto.setName(student.getFullName());
            dto.setDept(student.getDepartment());

            Optional<LeetCodeStats> statsOpt = leetCodeStatsRepository.findById(student.getUserId());
            if (statsOpt.isPresent()) {
                LeetCodeStats stats = statsOpt.get();
                dto.setTotal(stats.getTotalSolved() != null ? stats.getTotalSolved() : 0);
                dto.setEasy(stats.getEasySolved() != null ? stats.getEasySolved() : 0);
                dto.setMed(stats.getMediumSolved() != null ? stats.getMediumSolved() : 0);
                dto.setHard(stats.getHardSolved() != null ? stats.getHardSolved() : 0);
                dto.setRating(stats.getContestRating() != null ? stats.getContestRating() : 0);
            } else {
                dto.setTotal(0); dto.setEasy(0); dto.setMed(0); dto.setHard(0); dto.setRating(0);
            }
            dto.setToday(0);
            dto.setActivity(Arrays.asList(0, 0, 0, 0, 0, 0, 0));
            leaderboard.add(dto);
        }
        return ResponseEntity.ok(leaderboard);
    }
    
 // --- NEW: STUDENT OVERVIEW ENDPOINT ---
    @GetMapping("/students-overview")
    public ResponseEntity<List<StudentOverviewDTO>> getStudentsOverviewList() {
        try {
            // This calls the service method which aggregates basic student and LeetCode data.
            List<StudentOverviewDTO> overviewList = staffService.getAllStudentsOverview();
            return ResponseEntity.ok(overviewList);
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to generate Student Overview list. Error: " + e.getMessage());
            e.printStackTrace();
            // Return 500 status code to trigger the API Error message in the frontend.
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

 // --- NEW: STUDENT DETAILS ENDPOINT ---
    /**
     * STAFF: Fetches the comprehensive dashboard data for a single student ID.
     */
    @GetMapping("/student-details/{id}")
    public ResponseEntity<DashboardDTO> getStudentDetails(@PathVariable Long id) {
        try {
            // This calls the service method to aggregate all student stats (LC, HR, Logs, etc.)
            DashboardDTO dashboardData = staffService.getStudentDashboardData(id);
            return ResponseEntity.ok(dashboardData);
        } catch (RuntimeException e) {
            // Returns a 404 if the student ID is not found
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            System.err.println("Error fetching dashboard data for student ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // --- 2. GITHUB ANALYTICS ENDPOINT (Existing) ---
    @GetMapping("/github")
    public ResponseEntity<List<GitHubAnalyticsDTO>> getGitHubData() {
        List<StudentOverviewDTO> students = staffService.getAllStudentsOverview();

        List<GitHubAnalyticsDTO> analyticsList = students.stream()
            .map(student -> {
                try {
                    GitHubDTO stats = gitHubService.getUserStats(student.getUserId());

                    GitHubAnalyticsDTO dto = new GitHubAnalyticsDTO();
                    dto.setName(student.getFullName());
                    dto.setUserId(student.getUserId());

                    dto.setCommits(stats.getCommitsThisWeek() * 4);
                    dto.setRepos(stats.getPublicRepos());
                    dto.setFollowers(stats.getFollowers());
                    dto.setStars((int)(stats.getFollowers() * 0.5));
                    dto.setForks((int)(stats.getPublicRepos() * 1.5));

                    Random rand = new Random(stats.getUsername().hashCode());
                    List<Integer> activity = new ArrayList<>();
                    for (int i = 0; i < 7; i++) {
                        activity.add(rand.nextInt(5));
                    }
                    dto.setWeeklyActivity(activity);

                    Map<String, Integer> languageMap = new HashMap<>();
                    if (stats.getTopLanguages() != null) {
                        for (String lang : stats.getTopLanguages()) {
                            languageMap.put(lang, 1);
                        }
                    }
                    dto.setLanguages(languageMap);

                    return dto;
                } catch (Exception e) {
                    System.err.println("Failed to fetch GitHub data for user " + student.getFullName() + ": " + e.getMessage());
                    GitHubAnalyticsDTO emptyDto = new GitHubAnalyticsDTO();
                    emptyDto.setName(student.getFullName());
                    emptyDto.setUserId(student.getUserId());
                    return emptyDto;
                }
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(analyticsList);
    }

    // --- 3. HACKERRANK TRACKER ENDPOINT (Existing) ---
    @GetMapping("/hackerrank")
    public ResponseEntity<List<HackerRankTrackerDTO>> getHackerRankData() {
        List<StudentOverviewDTO> students = staffService.getAllStudentsOverview();
        List<HackerRankTrackerDTO> hrList = new ArrayList<>();

        for (StudentOverviewDTO student : students) {
            HackerRankTrackerDTO dto = new HackerRankTrackerDTO();
            dto.setId(student.getUserId());
            dto.setName(student.getFullName());

            // FETCH REAL DATA
            Optional<HackerRankStats> statsOpt = hackerRankStatsRepository.findById(student.getUserId());

            if (statsOpt.isPresent()) {
                HackerRankStats stats = statsOpt.get();

                int gold = stats.getGoldBadges() != null ? stats.getGoldBadges() : 0;
                int silver = stats.getSilverBadges() != null ? stats.getSilverBadges() : 0;
                int bronze = stats.getBronzeBadges() != null ? stats.getBronzeBadges() : 0;

                dto.setGoldBadges(gold);
                dto.setSilverBadges(silver);
                dto.setBronzeBadges(bronze);
                dto.setTotalBadges(gold + silver + bronze);

                Map<String, Integer> scores = new HashMap<>();
                scores.put("Algorithms", stats.getAlgorithmsScore() != null ? stats.getAlgorithmsScore() : 0);
                scores.put("Java", stats.getJavaScore() != null ? stats.getJavaScore() : 0);
                scores.put("SQL", stats.getSqlScore() != null ? stats.getSqlScore() : 0);
                scores.put("Python", stats.getPythonScore() != null ? stats.getPythonScore() : 0);
                dto.setDomainScores(scores);

                List<String> certsList = new ArrayList<>();
                if (stats.getCertificates() != null && !stats.getCertificates().isEmpty()) {
                    String[] arr = stats.getCertificates().split(",");
                    for (String c : arr) {
						certsList.add(c.trim());
					}
                }
                dto.setCertificates(certsList);
            } else {
                // Default 0s if no data
                dto.setTotalBadges(0);
                dto.setGoldBadges(0); dto.setSilverBadges(0); dto.setBronzeBadges(0);
                Map<String, Integer> emptyScores = new HashMap<>();
                emptyScores.put("Algorithms", 0); emptyScores.put("Java", 0);
                emptyScores.put("SQL", 0); emptyScores.put("Python", 0);
                dto.setDomainScores(emptyScores);
                dto.setCertificates(new ArrayList<>());
            }

            hrList.add(dto);
        }
        return ResponseEntity.ok(hrList);
    }

    // --- 4. CODING LOGS ENDPOINT (FINAL ROBUSTNESS CHECK) ---
    @GetMapping("/coding-logs")
    public ResponseEntity<CodingLogsResponseDTO> getCodingLogsReport() {
        try {
            // This calls the generateStaffReport() method which now includes streak calculation and aggregation
            CodingLogsResponseDTO response = codingLogService.generateStaffReport();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the error detail
            System.err.println("CRITICAL ERROR generating Coding Logs Report: " + e.getMessage());
            e.printStackTrace();

            // Return a safe, empty response to prevent client-side script failure
            CodingLogsResponseDTO emptyResponse = new CodingLogsResponseDTO();
            emptyResponse.setStudentLogs(Collections.emptyList());
            emptyResponse.setWeeklySummary(Collections.emptyList());
            // Return 200 OK with empty data, or use 500 if you prefer the frontend to show an error message.
            return ResponseEntity.ok(emptyResponse);
        }
    }

    // --- 5. STAFF GLOBAL SYNC ENDPOINT (FIXED METHOD CALL) ---
    /**
     * Staff-accessible endpoint to trigger global student data refresh.
     */
    @GetMapping("/trigger-refresh")
    public ResponseEntity<String> triggerGlobalDataRefresh() {
        try {
            // This method is expected to perform the following:
            // 1. Fetch all students.
            // 2. Loop through each student and call LeetCodeService to scrape data.
            // 3. LeetCodeService calls codingLogService.updateDailyLogsFromLeetCode().
            studentService.refreshAllStudentsData();

            return ResponseEntity.ok("Global data synchronization successfully triggered by staff. Please wait 30 seconds and refresh the Daily Coding Logs page.");
        } catch (Exception e) {
            System.err.println("Staff Global Sync failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Global sync failed: " + e.getMessage());
        }
    }

    // ===========================================
    // --- 6. RESUME REVIEW ENDPOINTS (NEW) ---
    // ===========================================

    // Endpoint for fetching *Pending* Resumes (Used by resume-review.html)
    @GetMapping("/resumes")
    public ResponseEntity<List<ResumeReviewDTO>> getPendingResumes() {
        List<Resume> pendingResumes = resumeService.getPendingResumes();

        List<ResumeReviewDTO> dtos = pendingResumes.stream()
            .map(ResumeReviewDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // *** NEW ENDPOINT: Fetch ALL Resumes (For History/Overview) ***
    @GetMapping("/resumes/all")
    public ResponseEntity<List<ResumeReviewDTO>> getAllResumesHistory() {
        List<Resume> allResumes = resumeService.getAllResumes();

        // Map Resume entities to ResumeReviewDTOs
        List<ResumeReviewDTO> dtos = allResumes.stream()
            .map(ResumeReviewDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * STAFF: POST /api/staff/resumes/approve/{id}
     */
    @PostMapping("/resumes/approve/{id}")
    public ResponseEntity<String> approveResume(@PathVariable Long id) {
        try {
            // Note: Null is passed for feedback since approval is simple
            resumeService.reviewResume(id, "approve", null);
            return ResponseEntity.ok("Resume approved successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * STAFF: POST /api/staff/resumes/reject/{id}
     */
    @PostMapping("/resumes/reject/{id}")
    public ResponseEntity<String> rejectResume(@PathVariable Long id) {
        try {
            // Note: Null is passed for feedback since rejection is simple
            resumeService.reviewResume(id, "reject", null);
            return ResponseEntity.ok("Resume rejected successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }

    }
    
 // ===========================================
 // --- 7. STAFF SETTINGS ENDPOINTS ---
 // ===========================================

     /**
      * STAFF: GET /api/staff/me
      * Loads the profile information (Name, Email) for the logged-in staff member.
      */
     @GetMapping("/me")
     public ResponseEntity<StaffSettingsDTO> getMyProfile(Principal principal) {
         try {
             // principal.getName() returns the username (email in many setups)
             String username = principal.getName();
             
             StaffSettingsDTO dto = staffService.getStaffProfile(username);
             
             // Note: Password is NOT returned for security reasons
             return ResponseEntity.ok(dto);
         } catch (RuntimeException e) {
             return ResponseEntity.status(404).body(null);
         }
     }

     /**
      * STAFF: PUT /api/staff/settings
      * Updates the staff member's name, email, and optionally, password.
      */
     @PutMapping("/settings") // Note: Must import @PutMapping and @RequestBody
     public ResponseEntity<?> updateSettings(@RequestBody StaffSettingsDTO settingsDto, Principal principal) {
         try {
             String username = principal.getName();
             
             staffService.updateStaffProfile(username, settingsDto);
             
             return ResponseEntity.ok("Profile and settings updated successfully.");
         } catch (RuntimeException e) {
             return ResponseEntity.status(400).body(e.getMessage());
         }
     }
     
     
  // ===========================================
     // --- 7. REPORTS & INSIGHTS ENDPOINTS (NEW) ---
     // ===========================================
     
     // NOTE: Requires importing the new report DTOs at the top of the file:
     // import dto.StudentReportDTO;
     // import dto.DepartmentGrowthDTO;
     // import dto.StudentMasterDTO;

     /**
      * STAFF: GET /api/staff/reports/monthly-skill
      * Fetches data for the Monthly Skill Report (CSV/Excel export).
      */
     @GetMapping("/reports/monthly-skill")
     public ResponseEntity<List<StudentReportDTO>> getMonthlySkillReport() {
         try {
             // Calls the service method to aggregate monthly skill data for all students
             List<StudentReportDTO> reportData = staffService.getMonthlySkillReportData();
             return ResponseEntity.ok(reportData);
         } catch (Exception e) {
             System.err.println("Error generating Monthly Skill Report: " + e.getMessage());
             return ResponseEntity.status(500).body(Collections.emptyList());
         }
     }

     /**
      * STAFF: GET /api/staff/reports/department-growth
      * Fetches data for the Department-Wise Skill Growth chart/table.
      */
     @GetMapping("/reports/department-growth")
     public ResponseEntity<List<DepartmentGrowthDTO>> getDepartmentGrowthReport() {
         try {
             // Calls the service method to aggregate department growth data
             List<DepartmentGrowthDTO> reportData = staffService.getDepartmentGrowthData();
             return ResponseEntity.ok(reportData);
         } catch (Exception e) {
             System.err.println("Error generating Department Growth Report: " + e.getMessage());
             return ResponseEntity.status(500).body(Collections.emptyList());
         }
     }

     /**
      * STAFF: GET /api/staff/reports/master-sheet
      * Fetches comprehensive raw data for the Full Student Master Sheet (PDF/CSV export).
      */
     @GetMapping("/reports/master-sheet")
     public ResponseEntity<List<StudentMasterDTO>> getFullStudentMasterSheet() {
         try {
             // Calls the service method to get the full raw student dataset
             List<StudentMasterDTO> reportData = staffService.getFullStudentMasterData();
             return ResponseEntity.ok(reportData);
         } catch (Exception e) {
             System.err.println("Error generating Master Sheet Report: " + e.getMessage());
             return ResponseEntity.status(500).body(Collections.emptyList());
         }
     }

     // ===========================================
     
}