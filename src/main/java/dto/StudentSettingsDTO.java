package dto;

public class StudentSettingsDTO {

    private String fullName;
    private String email;
    private String department;
    private String leetcodeUsername;
    private String githubLink;
    private String hackerrankLink;
    private String linkedinLink;

    // *** NEW FIELDS ADDED HERE ***
    private Integer linkedinConnections;
    private Integer linkedinFollowers;

    // --- Getters and Setters ---
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLeetcodeUsername() { return leetcodeUsername; }
    public void setLeetcodeUsername(String leetcodeUsername) { this.leetcodeUsername = leetcodeUsername; }

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }

    public String getHackerrankLink() { return hackerrankLink; }
    public void setHackerrankLink(String hackerrankLink) { this.hackerrankLink = hackerrankLink; }

    public String getLinkedinLink() { return linkedinLink; }
    public void setLinkedinLink(String linkedinLink) { this.linkedinLink = linkedinLink; }

    // *** NEW GETTERS AND SETTERS (This fixes the red lines) ***
    public Integer getLinkedinConnections() { return linkedinConnections; }
    public void setLinkedinConnections(Integer linkedinConnections) { this.linkedinConnections = linkedinConnections; }

    public Integer getLinkedinFollowers() { return linkedinFollowers; }
    public void setLinkedinFollowers(Integer linkedinFollowers) { this.linkedinFollowers = linkedinFollowers; }
}