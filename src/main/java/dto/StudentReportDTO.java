package dto;

// Used for Monthly Skill Report (CSV/Excel Export)
public class StudentReportDTO {
    private String fullName;
    private String department;
    private Integer leetcodeSolved;
    private Integer hackerRankBadges;
    private Integer githubRepos;
    private Integer verifiedCertificates;
    private Integer overallGrade; // From StaffService calculation

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getLeetcodeSolved() { return leetcodeSolved; }
    public void setLeetcodeSolved(Integer leetcodeSolved) { this.leetcodeSolved = leetcodeSolved; }

    public Integer getHackerRankBadges() { return hackerRankBadges; }
    public void setHackerRankBadges(Integer hackerRankBadges) { this.hackerRankBadges = hackerRankBadges; }

    public Integer getGithubRepos() { return githubRepos; }
    public void setGithubRepos(Integer githubRepos) { this.githubRepos = githubRepos; }

    public Integer getVerifiedCertificates() { return verifiedCertificates; }
    public void setVerifiedCertificates(Integer verifiedCertificates) { this.verifiedCertificates = verifiedCertificates; }

    public Integer getOverallGrade() { return overallGrade; }
    public void setOverallGrade(Integer overallGrade) { this.overallGrade = overallGrade; }
}