package dto;

import java.time.LocalDate;

public class RegisterDTO {

    // These names now match your HTML JavaScript exactly
    private String fullname; // HTML sends "fullname" (all lowercase)
    private String department;
    private String email;
    private String username;
    private String password;
    private LocalDate dob;

    private String leetcodeUsername;
    private String githubLink;
    private String hackerrankUsername; // HTML sends "hackerrankUsername"
    private String linkedinLink;
    private String role; // Added role field

    // --- Getters and Setters ---
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getLeetcodeUsername() { return leetcodeUsername; }
    public void setLeetcodeUsername(String leetcodeUsername) { this.leetcodeUsername = leetcodeUsername; }

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }

    public String getHackerrankUsername() { return hackerrankUsername; }
    public void setHackerrankUsername(String hackerrankUsername) { this.hackerrankUsername = hackerrankUsername; }

    public String getLinkedinLink() { return linkedinLink; }
    public void setLinkedinLink(String linkedinLink) { this.linkedinLink = linkedinLink; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}