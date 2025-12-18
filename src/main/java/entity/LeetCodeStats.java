package entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "leetcode_stats")
public class LeetCodeStats {

    // 1. SIMPLE MANUAL ID (We removed the User object link to stop the error)
	@Id
	@Column(name = "user_id", columnDefinition = "BIGINT") // <--- FIX HERE
	private Long userId;

    @Column(name = "total_solved")
    private Integer totalSolved;

    @Column(name = "easy_solved")
    private Integer easySolved;

    @Column(name = "medium_solved")
    private Integer mediumSolved;

    @Column(name = "hard_solved")
    private Integer hardSolved;

    @Column(name = "contest_rating")
    private Integer contestRating;

    @Lob
    @Column(name = "submission_calendar", columnDefinition = "LONGTEXT")
    private String submissionCalendar;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    // --- Getters and Setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // Note: We removed getUser/setUser to prevent Hibernate confusion

    public Integer getTotalSolved() { return totalSolved; }
    public void setTotalSolved(Integer totalSolved) { this.totalSolved = totalSolved; }
    public Integer getEasySolved() { return easySolved; }
    public void setEasySolved(Integer easySolved) { this.easySolved = easySolved; }
    public Integer getMediumSolved() { return mediumSolved; }
    public void setMediumSolved(Integer mediumSolved) { this.mediumSolved = mediumSolved; }
    public Integer getHardSolved() { return hardSolved; }
    public void setHardSolved(Integer hardSolved) { this.hardSolved = hardSolved; }
    public Integer getContestRating() { return contestRating; }
    public void setContestRating(Integer contestRating) { this.contestRating = contestRating; }
    public String getSubmissionCalendar() { return submissionCalendar; }
    public void setSubmissionCalendar(String submissionCalendar) { this.submissionCalendar = submissionCalendar; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}