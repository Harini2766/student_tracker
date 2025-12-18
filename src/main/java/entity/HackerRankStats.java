package entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "hackerrank_stats")
public class HackerRankStats {

	@Id
	@Column(name = "user_id", columnDefinition = "BIGINT") // <--- FIX HERE
	private Long userId;

    private Integer goldBadges;
    private Integer silverBadges;
    private Integer bronzeBadges;

    // Domain Scores
    private Integer algorithmsScore;
    private Integer javaScore;
    private Integer sqlScore;
    private Integer pythonScore;

    // Comma-separated list of certificates (e.g., "Java Basic,SQL Intermediate")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String certificates;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    // --- Getters & Setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getGoldBadges() { return goldBadges; }
    public void setGoldBadges(Integer goldBadges) { this.goldBadges = goldBadges; }

    public Integer getSilverBadges() { return silverBadges; }
    public void setSilverBadges(Integer silverBadges) { this.silverBadges = silverBadges; }

    public Integer getBronzeBadges() { return bronzeBadges; }
    public void setBronzeBadges(Integer bronzeBadges) { this.bronzeBadges = bronzeBadges; }

    public Integer getAlgorithmsScore() { return algorithmsScore; }
    public void setAlgorithmsScore(Integer algorithmsScore) { this.algorithmsScore = algorithmsScore; }

    public Integer getJavaScore() { return javaScore; }
    public void setJavaScore(Integer javaScore) { this.javaScore = javaScore; }

    public Integer getSqlScore() { return sqlScore; }
    public void setSqlScore(Integer sqlScore) { this.sqlScore = sqlScore; }

    public Integer getPythonScore() { return pythonScore; }
    public void setPythonScore(Integer pythonScore) { this.pythonScore = pythonScore; }

    public String getCertificates() { return certificates; }
    public void setCertificates(String certificates) { this.certificates = certificates; }
}