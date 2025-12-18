package dto;

// Used for Full Student Master Sheet (PDF Export)
public class StudentMasterDTO {
    private String fullName;
    private String department;
    private String email;
    private Integer totalLeetCodeSolved;
    private Integer totalHackerRankBadges;
    private Integer totalGitHubRepos;
    private String resumeStatus;
    private String latestCertificateName;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getTotalLeetCodeSolved() { return totalLeetCodeSolved; }
    public void setTotalLeetCodeSolved(Integer totalLeetCodeSolved) { this.totalLeetCodeSolved = totalLeetCodeSolved; }

    public Integer getTotalHackerRankBadges() { return totalHackerRankBadges; }
    public void setTotalHackerRankBadges(Integer totalHackerRankBadges) { this.totalHackerRankBadges = totalHackerRankBadges; }

    public Integer getTotalGitHubRepos() { return totalGitHubRepos; }
    public void setTotalGitHubRepos(Integer totalGitHubRepos) { this.totalGitHubRepos = totalGitHubRepos; }

    public String getResumeStatus() { return resumeStatus; }
    public void setResumeStatus(String resumeStatus) { this.resumeStatus = resumeStatus; }

    public String getLatestCertificateName() { return latestCertificateName; }
    public void setLatestCertificateName(String latestCertificateName) { this.latestCertificateName = latestCertificateName; }
}