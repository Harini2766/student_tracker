package dto;

import java.util.List;
import entity.LeetCodeStats;
import entity.HackerRankStats;
import entity.StudentProfile; 
import entity.Certificate;   

import java.util.Map;

public class DashboardDTO {
    
    // --- 1. Top-Level Student Info (Used by StudentService and StaffService) ---
    private Long userId; 
    private String fullName;
    private String department;
    private String lastSynced;
    private String studentName; 

    // --- 2. Simple Counts/Status (Used directly by StudentService) ---
    private Integer leetcodeSolved;
    private Integer githubRepos;
    private Integer hackerRankBadges;
    private Integer certificatesCount;
    private String resumeStatus;
    private Integer totalDailyCoding; 

    // --- 3. Nested Data Objects (Detailed information - Used by StaffService for Details Page) ---
    private LeetCodeStats leetCodeStats;
    private HackerRankStats hackerRankStats;
    private StudentProfile studentProfile; 
    private List<Certificate> certificateList; 
    
    // --- 4. Calculated Metrics (Used by StaffService) ---
    private Integer overallGrade; 
    private DailyCodingDTO dailyCodingData; 

    // --- 5. Class-Wide Stats (For the Staff Dashboard metrics) ---
    private Integer totalStudents;
    private Integer classTotalLeetCode;
    private Integer classTotalCertificates;
    private String topPerformerName;
    private Integer myRank;
    private String mostActiveDept;
    
	 // --- 6. New Staff Dashboard Fields (Previously section 6) ---
	 private Integer classAverageCodingScore;
	 private List<StudentOverviewDTO> topPerformers; 
	 private Map<String, Long> skillDistribution;

     // *** NEW FIELDS FOR ENHANCED STAFF DASHBOARD ***
//     private Integer pendingResumesCount;
     private Integer pendingCertsCount;
     private Integer classTotalGitHubRepos;


    // --- Getters and Setters for ALL fields ---
    
    // Top-Level Getters/Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getLastSynced() { return lastSynced; }
    public void setLastSynced(String lastSynced) { this.lastSynced = lastSynced; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Integer getLeetcodeSolved() { return leetcodeSolved; }
    public void setLeetcodeSolved(Integer leetcodeSolved) { this.leetcodeSolved = leetcodeSolved; }

    public Integer getGithubRepos() { return githubRepos; }
    public void setGithubRepos(Integer githubRepos) { this.githubRepos = githubRepos; }
    
    public Integer getHackerRankBadges() { return hackerRankBadges; }
    public void setHackerRankBadges(Integer hackerRankBadges) { this.hackerRankBadges = hackerRankBadges; }

    public Integer getCertificatesCount() { return certificatesCount; }
    public void setCertificatesCount(Integer certificatesCount) { this.certificatesCount = certificatesCount; }

    public String getResumeStatus() { return resumeStatus; }
    public void setResumeStatus(String resumeStatus) { this.resumeStatus = resumeStatus; }

    public Integer getTotalDailyCoding() { return totalDailyCoding; }
    public void setTotalDailyCoding(Integer totalDailyCoding) { this.totalDailyCoding = totalDailyCoding; }
    
    // Nested Data Getters/Setters
    public LeetCodeStats getLeetCodeStats() { return leetCodeStats; }
    public void setLeetCodeStats(LeetCodeStats leetCodeStats) { this.leetCodeStats = leetCodeStats; }

    public HackerRankStats getHackerRankStats() { return hackerRankStats; }
    public void setHackerRankStats(HackerRankStats hackerRankStats) { this.hackerRankStats = hackerRankStats; }

    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile studentProfile) { this.studentProfile = studentProfile; }
    
    public List<Certificate> getCertificateList() { return certificateList; }
    public void setCertificateList(List<Certificate> certificateList) { this.certificateList = certificateList; }
    
    // Calculated/Aggregated Getters/Setters
    public Integer getOverallGrade() { return overallGrade; }
    public void setOverallGrade(Integer overallGrade) { this.overallGrade = overallGrade; }

    public DailyCodingDTO getDailyCodingData() { return dailyCodingData; }
    public void setDailyCodingData(DailyCodingDTO dailyCodingData) { this.dailyCodingData = dailyCodingData; }
    
    // Class Stats Getters/Setters (Existing)
    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }

    public Integer getClassTotalLeetCode() { return classTotalLeetCode; }
    public void setClassTotalLeetCode(Integer classTotalLeetCode) { this.classTotalLeetCode = classTotalLeetCode; }

    public Integer getClassTotalCertificates() { return classTotalCertificates; }
    public void setClassTotalCertificates(Integer classTotalCertificates) { this.classTotalCertificates = classTotalCertificates; }

    public String getTopPerformerName() { return topPerformerName; }
    public void setTopPerformerName(String topPerformerName) { this.topPerformerName = topPerformerName; }

    public Integer getMyRank() { return myRank; }
    public void setMyRank(Integer myRank) { this.myRank = myRank; }

    public String getMostActiveDept() { return mostActiveDept; }
    public void setMostActiveDept(String mostActiveDept) { this.mostActiveDept = mostActiveDept; }
    
    public Integer getClassAverageCodingScore() { return classAverageCodingScore; }
    public void setClassAverageCodingScore(Integer classAverageCodingScore) { this.classAverageCodingScore = classAverageCodingScore; }

    public List<StudentOverviewDTO> getTopPerformers() { return topPerformers; }
    public void setTopPerformers(List<StudentOverviewDTO> topPerformers) { this.topPerformers = topPerformers; }

    public Map<String, Long> getSkillDistribution() { return skillDistribution; }
    public void setSkillDistribution(Map<String, Long> skillDistribution) { this.skillDistribution = skillDistribution; }
    
    // *** NEW GETTERS AND SETTERS ***
//    public Integer getPendingResumesCount() { return pendingResumesCount; }
//    public void setPendingResumesCount(Integer pendingResumesCount) { this.pendingResumesCount = pendingResumesCount; }

    public Integer getPendingCertsCount() { return pendingCertsCount; }
    public void setPendingCertsCount(Integer pendingCertsCount) { this.pendingCertsCount = pendingCertsCount; }

    public Integer getClassTotalGitHubRepos() { return classTotalGitHubRepos; }
    public void setClassTotalGitHubRepos(Integer classTotalGitHubRepos) { this.classTotalGitHubRepos = classTotalGitHubRepos; }
}