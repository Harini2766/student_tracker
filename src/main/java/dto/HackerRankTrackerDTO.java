package dto;

import java.util.List;
import java.util.Map;

public class HackerRankTrackerDTO {
    private Long id;
    private String name;
    private Integer totalBadges;
    // Badge Breakdown
    private Integer goldBadges;
    private Integer silverBadges;
    private Integer bronzeBadges;
    // Skill Scores (e.g., "SQL" -> 500 points)
    private Map<String, Integer> domainScores;
    // List of Certificate Names (e.g., "Java (Basic)")
    private List<String> certificates;

    public HackerRankTrackerDTO() {}

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getTotalBadges() { return totalBadges; }
    public void setTotalBadges(Integer totalBadges) { this.totalBadges = totalBadges; }
    public Integer getGoldBadges() { return goldBadges; }
    public void setGoldBadges(Integer goldBadges) { this.goldBadges = goldBadges; }
    public Integer getSilverBadges() { return silverBadges; }
    public void setSilverBadges(Integer silverBadges) { this.silverBadges = silverBadges; }
    public Integer getBronzeBadges() { return bronzeBadges; }
    public void setBronzeBadges(Integer bronzeBadges) { this.bronzeBadges = bronzeBadges; }
    public Map<String, Integer> getDomainScores() { return domainScores; }
    public void setDomainScores(Map<String, Integer> domainScores) { this.domainScores = domainScores; }
    public List<String> getCertificates() { return certificates; }
    public void setCertificates(List<String> certificates) { this.certificates = certificates; }
}