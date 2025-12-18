package dto;

// This DTO is used to list students on the staff's overview page (students-overview.html)
public class StudentOverviewDTO {

    private Long userId;
    private String fullName;
    private String department;
    private String email;
    private Integer leetcodeSolved; // Key metric for staff overview

    // --- Getters and Setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getLeetcodeSolved() { return leetcodeSolved; }
    public void setLeetcodeSolved(Integer leetcodeSolved) { this.leetcodeSolved = leetcodeSolved; }
}