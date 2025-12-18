package dto;

public class LeetCodeDTO {
    private Integer totalSolved;
    private Integer easySolved;
    private Integer mediumSolved;
    private Integer hardSolved;
    private Integer ranking;
    private String acceptanceRate;
    private Integer contributionPoints;

    // *** NEW FIELD ***
    private String submissionCalendar;

    public LeetCodeDTO() {}

    // Getters and Setters
    public Integer getTotalSolved() { return totalSolved; }
    public void setTotalSolved(Integer totalSolved) { this.totalSolved = totalSolved; }
    public Integer getEasySolved() { return easySolved; }
    public void setEasySolved(Integer easySolved) { this.easySolved = easySolved; }
    public Integer getMediumSolved() { return mediumSolved; }
    public void setMediumSolved(Integer mediumSolved) { this.mediumSolved = mediumSolved; }
    public Integer getHardSolved() { return hardSolved; }
    public void setHardSolved(Integer hardSolved) { this.hardSolved = hardSolved; }
    public Integer getRanking() { return ranking; }
    public void setRanking(Integer ranking) { this.ranking = ranking; }
    public String getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(String acceptanceRate) { this.acceptanceRate = acceptanceRate; }
    public Integer getContributionPoints() { return contributionPoints; }
    public void setContributionPoints(Integer contributionPoints) { this.contributionPoints = contributionPoints; }

    public String getSubmissionCalendar() { return submissionCalendar; }
    public void setSubmissionCalendar(String submissionCalendar) { this.submissionCalendar = submissionCalendar; }
}