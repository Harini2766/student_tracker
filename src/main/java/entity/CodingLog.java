package entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- NEW IMPORT

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "daily_coding_logs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "log_date"})
})
public class CodingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

 // Inside entity/CodingLog.java

    @Column(name = "user_id", nullable = false, columnDefinition = "INT") // <--- ADD THIS
    private Long userId;

    // Relation to User (Read-only for JPA to avoid conflicts)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore // <-- CRITICAL FIX: Stops the infinite recursion loop in JSON conversion
    private User user;

    @Column(name = "solved_count", nullable = false)
    private Integer solvedCount;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    // --- Getters and Setters ---
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getSolvedCount() { return solvedCount; }
    public void setSolvedCount(Integer solvedCount) { this.solvedCount = solvedCount; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
}