package service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.DailyCodingDTO;
import dto.DashboardDTO;
import dto.LeaderboardDTO;
import dto.StudentSettingsDTO;

import dto.LeetCodeDTO;

import entity.StudentProfile;
import entity.User;
import repository.CertificateRepository;
import repository.CodingLogRepository;
import repository.LeetCodeStatsRepository;
import repository.StudentProfileRepository;
import repository.UserRepository;

@Service
public class StudentService {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentProfileRepository studentProfileRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private LeetCodeStatsRepository leetCodeStatsRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private CodingLogRepository codingLogRepository;

    @Autowired private CodingLogService codingLogService;

    // *** INJECTED DEPENDENCIES FOR REFRESH ***
    @Autowired private LeetCodeService leetCodeService;
    @Autowired private HackerRankService hackerRankService;
    @Autowired private GitHubService gitHubService;

    public DashboardDTO getDashboardData(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found."));
        StudentProfile profile = studentProfileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found."));

        DashboardDTO dto = new DashboardDTO();

        // 1. Personal Data
        dto.setStudentName(user.getFullName());

        int myLc = leetCodeStatsRepository.findByUserId(userId).map(s -> s.getTotalSolved()).orElse(0);
        dto.setLeetcodeSolved(myLc);
        dto.setGithubRepos(profile.getGithubRepos());
        dto.setHackerRankBadges(profile.getHackerrankBadges());
        dto.setCertificatesCount(certificateRepository.findByUserId(userId).size());
        dto.setResumeStatus(profile.getResumeStatus());

        DailyCodingDTO dailyStats = codingLogService.getStudentStats(userId);
        dto.setTotalDailyCoding(dailyStats.getTodayCount());

        // 2. Class-Wide Data
        dto.setTotalStudents((int) studentProfileRepository.count());

        int classLc = leetCodeStatsRepository.findAll().stream().mapToInt(s -> s.getTotalSolved()).sum();
        dto.setClassTotalLeetCode(classLc);

        dto.setClassTotalCertificates((int) certificateRepository.count());

        List<LeaderboardDTO> leaderboard = getLeaderboard();

        if (!leaderboard.isEmpty()) {
            dto.setTopPerformerName(leaderboard.get(0).getStudentName());
        } else {
            dto.setTopPerformerName("N/A");
        }

        int rank = 0;
        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getStudentName().equals(user.getFullName())) {
                rank = i + 1;
                break;
            }
        }
        dto.setMyRank(rank > 0 ? rank : leaderboard.size() + 1);
        dto.setMostActiveDept("CSE");

        return dto;
    }

    /**
     * UPDATED LEADERBOARD: Uses CodingLogService to get the real "Today" count.
     */
    public List<LeaderboardDTO> getLeaderboard() {
        return studentProfileRepository.findAll().stream()
            .map(profile -> {
                Long uid = profile.getUserId();

                // 1. LeetCode (Live)
                int liveLc = leetCodeStatsRepository.findByUserId(uid)
                        .map(s -> s.getTotalSolved()).orElse(0);

                // 2. Certificates (Live)
                int liveCerts = certificateRepository.findByUserId(uid).size();

                // 3. Daily Coding (Live - Today's Count)
                int todayCount = codingLogService.getStudentStats(uid).getTodayCount();

                return new LeaderboardDTO(
                    profile.getUser().getFullName(),
                    profile.getUser().getDepartment(),
                    liveLc,
                    profile.getGithubRepos(),
                    profile.getHackerrankBadges(),
                    liveCerts,
                    profile.getLinkedinConnections(),
                    todayCount
                );
            })
            .sorted(Comparator.comparingInt(LeaderboardDTO::getTotalScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }

    /**
     * Loops through all students and triggers a data refresh for all platforms (LeetCode, HR, GitHub).
     * This method is called by the /api/staff/trigger-refresh endpoint.
     */
    public void refreshAllStudentsData() {
        // CRITICAL FIX: Use the explicit JPA query method to avoid MySQL ENUM mapping crash
        List<User> students = userRepository.findStudentUsersForReport();

        // Use CompletableFuture to run all syncs concurrently for speed
        List<CompletableFuture<Void>> futures = students.stream()
            .map(user -> CompletableFuture.runAsync(() -> {
                Long userId = user.getUserId();
                String username = user.getUsername();

                // *** CRITICAL: Each sync is now inside its own try-catch block ***
                // This ensures one failure (e.g., LeetCode API is down) doesn't skip
                // the other syncs (e.g., GitHub and HackerRank).

                try {
                    // 1. Refresh LeetCode
                    leetCodeService.refreshLeetCodeData(userId);
                    System.out.println("✅ LeetCode sync OK for " + username);
                } catch (Exception e) {
                    System.err.println("❌ LeetCode sync failed for " + username + ": " + e.getMessage());
                }

                try {
                    // 2. Refresh GitHub
                    gitHubService.refreshGitHubData(userId);
                    System.out.println("✅ GitHub sync OK for " + username);
                } catch (Exception e) {
                    System.err.println("❌ GitHub sync failed for " + username + ": " + e.getMessage());
                }

                try {
                    // 3. Refresh HackerRank
                    hackerRankService.refreshHackerRankData(userId);
                    System.out.println("✅ HackerRank sync OK for " + username);
                } catch (Exception e) {
                    System.err.println("❌ HackerRank sync failed for " + username + ": " + e.getMessage());
                }
            }))
            .collect(Collectors.toList());

        // Wait for all tasks to complete before exiting the method
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("✅ Global Sync loop finished.");
    }

    // --- Settings & Auth ---
    /**
     * Correctly fetches user settings data and populates the DTO.
     * This fixes the compiler error from attempting to use write logic in a read method.
     */
    public StudentSettingsDTO getSettings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        StudentProfile profile = studentProfileRepository.findById(userId).orElseThrow();
        StudentSettingsDTO dto = new StudentSettingsDTO();

        // 1. Read User Data
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setDepartment(user.getDepartment());

        // 2. Read Profile Data
        dto.setLeetcodeUsername(profile.getLeetcodeUsername());
        dto.setGithubLink(profile.getGithubLink());
        dto.setHackerrankLink(profile.getHackerrankLink());
        dto.setLinkedinLink(profile.getLinkedinLink());

        // 3. Read LinkedIn data (the previously problematic lines, now correctly reading from profile)
        dto.setLinkedinConnections(profile.getLinkedinConnections());
        dto.setLinkedinFollowers(profile.getLinkedinFollowers());

        return dto;
    }

    @Transactional
    public void updateSettings(Long userId, StudentSettingsDTO settingsDto) {
        User user = userRepository.findById(userId).orElseThrow();
        StudentProfile profile = studentProfileRepository.findById(userId).orElseThrow();
        user.setFullName(settingsDto.getFullName());
        user.setEmail(settingsDto.getEmail());
        user.setDepartment(settingsDto.getDepartment());
        userRepository.save(user);
        profile.setLeetcodeUsername(settingsDto.getLeetcodeUsername());
        profile.setGithubLink(settingsDto.getGithubLink());
        profile.setHackerrankLink(settingsDto.getHackerrankLink());
        profile.setLinkedinLink(settingsDto.getLinkedinLink());
        if(settingsDto.getLinkedinConnections() != null) {
			profile.setLinkedinConnections(settingsDto.getLinkedinConnections());
		}
        if(settingsDto.getLinkedinFollowers() != null) {
			profile.setLinkedinFollowers(settingsDto.getLinkedinFollowers());
		}
        studentProfileRepository.save(profile);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}