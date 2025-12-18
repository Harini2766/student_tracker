package service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator; 
import java.util.Map; 
import java.util.HashMap; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.DashboardDTO;
import dto.StaffSettingsDTO; 
import dto.StudentOverviewDTO;
// --- NEW IMPORTS REQUIRED FOR DATA AGGREGATION ---
import entity.LeetCodeStats;
import entity.User;
import entity.HackerRankStats; 
import entity.StudentProfile; 
import entity.Certificate;   
import entity.Resume; // NEW IMPORT: Required for Resume count
import repository.LeetCodeStatsRepository;
import repository.UserRepository;
import repository.HackerRankStatsRepository; 
import repository.StudentProfileRepository; 
import repository.CertificateRepository;   
import repository.ResumeRepository; // NEW IMPORT: Required for Resume count

// --- NEW IMPORTS FOR REPORTS ---
import dto.StudentReportDTO; 
import dto.DepartmentGrowthDTO; 
import dto.StudentMasterDTO; 

@Service
public class StaffService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentService studentService; 

    @Autowired
    private LeetCodeStatsRepository leetCodeStatsRepository;
    
    @Autowired
    private HackerRankStatsRepository hackerRankStatsRepository; 
    
    @Autowired
    private StudentProfileRepository studentProfileRepository; 
    
    @Autowired
    private CertificateRepository certificateRepository; 
    
    @Autowired
    private PasswordEncoder passwordEncoder; 

//    @Autowired
//    private ResumeRepository resumeRepository; // NEW INJECTION

    public List<StudentOverviewDTO> getAllStudentsOverview() {
        List<User> students = userRepository.findStudentUsersForReport();

        return students.stream()
            .map(user -> {
                StudentOverviewDTO dto = new StudentOverviewDTO();
                dto.setUserId(user.getUserId());
                dto.setFullName(user.getFullName());
                dto.setDepartment(user.getDepartment());
                dto.setEmail(user.getEmail());

                Optional<LeetCodeStats> stats = leetCodeStatsRepository.findByUserId(user.getUserId());
                dto.setLeetcodeSolved(stats.map(LeetCodeStats::getTotalSolved).orElse(0));

                return dto;
            })
            .collect(Collectors.toList());
    }

    public DashboardDTO getStudentDashboardData(Long studentId) {

        User user = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found."));

        if (!user.getRole().name().equals("STUDENT")) {
             throw new RuntimeException("User is not a student.");
        }
        
        DashboardDTO dto = new DashboardDTO();
        dto.setUserId(studentId);
        dto.setFullName(user.getFullName());
        dto.setDepartment(user.getDepartment());

        Optional<LeetCodeStats> lcStatsOpt = leetCodeStatsRepository.findByUserId(studentId);
        Optional<HackerRankStats> hrStatsOpt = hackerRankStatsRepository.findByUserId(studentId);
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUserId(studentId);
        List<Certificate> certs = certificateRepository.findByUserId(studentId);
        
        if(lcStatsOpt.isPresent()) {
            LeetCodeStats lcStats = lcStatsOpt.get();
            dto.setLeetCodeStats(lcStats);
            
            int totalSolved = lcStats.getTotalSolved() != null ? lcStats.getTotalSolved() : 0;
            dto.setOverallGrade(calculateOverallGrade(totalSolved));
        }

        if(hrStatsOpt.isPresent()) {
            dto.setHackerRankStats(hrStatsOpt.get());
        }
        
        if(profileOpt.isPresent()) {
            dto.setStudentProfile(profileOpt.get());
        }

        dto.setCertificateList(certs);
        
        if (dto.getOverallGrade() == null) {
            dto.setOverallGrade(calculateOverallGrade(0)); 
        }
        
        return dto;
    }
    
public DashboardDTO getStaffDashboardMetrics() {
        
        DashboardDTO dto = new DashboardDTO();

        // 1. Fetch ALL Data required for aggregation
        List<User> students = userRepository.findStudentUsersForReport();
        List<LeetCodeStats> allLcStats = leetCodeStatsRepository.findAll();
        List<Certificate> allCerts = certificateRepository.findAll();
        List<StudentProfile> allProfiles = studentProfileRepository.findAll();
//        List<Resume> allResumes = resumeRepository.findAll(); // NEW: Fetch all resumes

        // 2. Simple Metrics Calculation
        
        // Total Students
        dto.setTotalStudents(students.size());
        
        // Calculate Average College Coding Score
        double averageScore = allLcStats.stream()
            .mapToInt(s -> calculateOverallGrade(s.getTotalSolved() != null ? s.getTotalSolved() : 0))
            .average()
            .orElse(0.0);
        
        dto.setClassAverageCodingScore((int) Math.round(averageScore));
        
        // Total Certificates (Overall - not used on dashboard, but kept for legacy)
        // dto.setClassTotalCertificates(allCerts.size()); 
        
        // *** NEW METRICS CALCULATION ***
        
//        // Pending Resumes Count
//        long pendingResumes = allResumes.stream()
//            .filter(r -> r.getStatus() != null && r.getStatus().name().equals("PENDING"))
//            .count();
//        dto.setPendingResumesCount((int) pendingResumes);

        // Pending Certificates Count
        long pendingCerts = allCerts.stream()
            .filter(c -> c.getStatus() != null && c.getStatus().name().equals("PENDING"))
            .count();
        dto.setPendingCertsCount((int) pendingCerts);

        // Total GitHub Repos
        int totalGitHubRepos = allProfiles.stream()
            .mapToInt(p -> p.getGithubRepos() != null ? p.getGithubRepos() : 0)
            .sum();
        dto.setClassTotalGitHubRepos(totalGitHubRepos);

        // 3. Leaderboard (Top Performer List)
        List<StudentOverviewDTO> studentOverview = getAllStudentsOverview();
        
        List<StudentOverviewDTO> sortedStudents = studentOverview.stream()
            .sorted(Comparator.comparingInt(StudentOverviewDTO::getLeetcodeSolved).reversed())
            .limit(5) 
            .collect(Collectors.toList());

        // Populate Top Performers and Top Performer Name
        dto.setTopPerformers(sortedStudents);
        if (!sortedStudents.isEmpty()) {
            dto.setTopPerformerName(sortedStudents.get(0).getFullName());
        }

        // 4. Skill Distribution Data (Required for Chart 1)
        long easy = allLcStats.stream().mapToLong(s -> s.getEasySolved() != null ? s.getEasySolved() : 0).sum();
        long medium = allLcStats.stream().mapToLong(s -> s.getMediumSolved() != null ? s.getMediumSolved() : 0).sum();
        long hard = allLcStats.stream().mapToLong(s -> s.getHardSolved() != null ? s.getHardSolved() : 0).sum();

        Map<String, Long> skillDistribution = new HashMap<>();
        skillDistribution.put("Easy", easy);
        skillDistribution.put("Medium", medium);
        skillDistribution.put("Hard", hard);
        
        dto.setSkillDistribution(skillDistribution);

        return dto;
    }

    private Integer calculateOverallGrade(int leetcodeSolved) {
        int score = leetcodeSolved * 2; 
        return Math.min(score, 1000);
    }

    public List<StudentReportDTO> getMonthlySkillReportData() {
        
        List<User> students = userRepository.findStudentUsersForReport();

        return students.stream()
                .map(user -> {
                    StudentReportDTO dto = new StudentReportDTO(); 
                    dto.setFullName(user.getFullName());
                    dto.setDepartment(user.getDepartment());

                    Long userId = user.getUserId();
                    Optional<LeetCodeStats> lcStatsOpt = leetCodeStatsRepository.findByUserId(userId);
                    Optional<HackerRankStats> hrStatsOpt = hackerRankStatsRepository.findByUserId(userId);
                    List<Certificate> certs = certificateRepository.findByUserId(userId);
                    
                    int totalSolved = lcStatsOpt.map(s -> s.getTotalSolved()).orElse(0);
                    
                    dto.setLeetcodeSolved(totalSolved);
                    dto.setHackerRankBadges(hrStatsOpt.map(s -> s.getGoldBadges() + s.getSilverBadges() + s.getBronzeBadges()).orElse(0));
                    dto.setVerifiedCertificates((int) certs.stream().filter(c -> c.getStatus().name().equals("VERIFIED")).count());
                    dto.setOverallGrade(calculateOverallGrade(totalSolved));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DepartmentGrowthDTO> getDepartmentGrowthData() {
        
        List<User> students = userRepository.findStudentUsersForReport();

        Map<String, List<User>> studentsByDept = students.stream()
            .collect(Collectors.groupingBy(User::getDepartment));

        return studentsByDept.entrySet().stream()
            .map(entry -> {
                String department = entry.getKey();
                List<User> deptUsers = entry.getValue();
                
                List<Integer> grades = deptUsers.stream()
                    .map(user -> {
                        Optional<LeetCodeStats> lcStatsOpt = leetCodeStatsRepository.findByUserId(user.getUserId());
                        int totalSolved = lcStatsOpt.map(s -> s.getTotalSolved()).orElse(0);
                        return calculateOverallGrade(totalSolved);
                    })
                    .collect(Collectors.toList());

                double averageGrade = grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                
                DepartmentGrowthDTO dto = new DepartmentGrowthDTO();
                dto.setDepartmentName(department);
                dto.setTotalStudents(deptUsers.size());
                dto.setCurrentAverageGrade((int) Math.round(averageGrade));
                
                dto.setMonthlyAverageGrade(null); 
                
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    public List<StudentMasterDTO> getFullStudentMasterData() {
        
        List<User> students = userRepository.findStudentUsersForReport();

        return students.stream()
                .map(user -> {
                    Long userId = user.getUserId();
                    StudentMasterDTO dto = new StudentMasterDTO(); 
                    
                    dto.setFullName(user.getFullName());
                    dto.setDepartment(user.getDepartment());
                    dto.setEmail(user.getEmail());
                    
                    leetCodeStatsRepository.findByUserId(userId).ifPresent(lc -> {
                        dto.setTotalLeetCodeSolved(lc.getTotalSolved() != null ? lc.getTotalSolved() : 0);
                    });
                    
                    hackerRankStatsRepository.findByUserId(userId).ifPresent(hr -> {
                        int totalBadges = (hr.getGoldBadges() != null ? hr.getGoldBadges() : 0) +
                                          (hr.getSilverBadges() != null ? hr.getSilverBadges() : 0) +
                                          (hr.getBronzeBadges() != null ? hr.getBronzeBadges() : 0);
                        dto.setTotalHackerRankBadges(totalBadges);
                    });
                    
                    studentProfileRepository.findByUserId(userId).ifPresent(profile -> {
                        dto.setTotalGitHubRepos(profile.getGithubRepos() != null ? profile.getGithubRepos() : 0);
                        dto.setResumeStatus(profile.getResumeStatus() != null ? profile.getResumeStatus() : "Not Uploaded");
                    });
                    
                    // --- 3. Certificates (Simple Logic to avoid date comparison crash) ---
                    List<Certificate> certs = certificateRepository.findByUserId(userId);
                    if (!certs.isEmpty()) {
                        // Safety: Just return the name of the first verified certificate found
                        Optional<Certificate> verifiedCert = certs.stream()
                                .filter(c -> c.getStatus().name().equals("VERIFIED"))
                                .findFirst();
                        
                        if (verifiedCert.isPresent()) {
                             dto.setLatestCertificateName(verifiedCert.get().getName());
                        } else {
                             // If none are verified, just take the first one found
                             dto.setLatestCertificateName(certs.get(0).getName() + " (Pending)");
                        }
                    } else {
                        dto.setLatestCertificateName("None");
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public StaffSettingsDTO getStaffProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Staff user not found."));
            
        if (!user.getRole().name().equals("STAFF")) {
            throw new RuntimeException("Access Denied: User is not a staff member.");
        }
        
        return new StaffSettingsDTO(user.getFullName(), user.getEmail());
    }

    @Transactional
    public void updateStaffProfile(String username, StaffSettingsDTO settingsDto) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Staff user not found."));

        if (!user.getRole().name().equals("STAFF")) {
            throw new RuntimeException("Access Denied: Cannot update non-staff account via this endpoint.");
        }

        if (settingsDto.getFullName() != null && !settingsDto.getFullName().trim().isEmpty()) {
            user.setFullName(settingsDto.getFullName().trim());
        }
        
        if (settingsDto.getEmail() != null && !settingsDto.getEmail().trim().isEmpty()) {
            String newEmail = settingsDto.getEmail().trim();
            user.setEmail(newEmail);
            user.setUsername(newEmail);
        }

        String newPassword = settingsDto.getPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                throw new RuntimeException("New password must be at least 6 characters long.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }
}