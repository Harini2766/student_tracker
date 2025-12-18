package dto;

import entity.Resume;

// DTO specifically for the Staff Resume Review Page
public class ResumeReviewDTO {

    private Long id;
    private String student;
    private String url; // URL to view the PDF file

    public ResumeReviewDTO(Resume resume) {
        this.id = resume.getResumeId();
        // Assuming your Resume entity has a linked User object with getFullName()
        // We use the User associated with the Resume
        this.student = resume.getUser().getFullName();

        // This generates the URL for the frontend's iframe and full-screen link
        this.url = "/api/resume/download/" + resume.getResumeId();
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudent() { return student; }
    public void setStudent(String student) { this.student = student; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}