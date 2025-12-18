package dto;

import java.time.LocalDate;

import entity.Certificate;

public class CertificateVerificationDTO {

    private Long id;
    private String student;
    private String title;
    private LocalDate issueDate;
    private String status;
    private String url; // URL to view the file

    // Constructor to map Certificate entity to DTO
    public CertificateVerificationDTO(Certificate certificate) {
        this.id = certificate.getId();
        // Assuming your Certificate entity has a linked User object with getFullName()
        this.student = certificate.getUser().getFullName();
        this.title = certificate.getName();
        this.issueDate = certificate.getIssueDate();
        this.status = certificate.getStatus().name();

        // This is the URL the staff member will use to view the PDF/Image
        this.url = "http://localhost:9091/api/certificates/download/" + certificate.getId();
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudent() { return student; }
    public void setStudent(String student) { this.student = student; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}