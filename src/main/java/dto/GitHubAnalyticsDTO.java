package dto;

import java.util.List;
import java.util.Map;

public class GitHubAnalyticsDTO {
    private Long userId;
    private String name;

    // Overview KPIs
    private Integer commits; // Total Commits
    private Integer repos;   // Total Repos
    private List<Integer> weeklyActivity; // Last 7 days/weeks activity

    // Repo Stats KPIs
    private Integer stars;      // Total Stars
    private Integer forks;      // Total Forks (Mocked as stars/2)
    private Integer followers;  // Total Followers

    // Language Analysis
    private Map<String, Integer> languages; // Map of Language -> count

    // Constructor (empty)
    public GitHubAnalyticsDTO() {
        this.commits = 0;
        this.repos = 0;
        this.stars = 0;
        this.forks = 0;
        this.followers = 0;
    }

    // --- Getters and Setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCommits() { return commits; }
    public void setCommits(Integer commits) { this.commits = commits; }
    public Integer getRepos() { return repos; }
    public void setRepos(Integer repos) { this.repos = repos; }
    public List<Integer> getWeeklyActivity() { return weeklyActivity; }
    public void setWeeklyActivity(List<Integer> weeklyActivity) { this.weeklyActivity = weeklyActivity; }
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public Integer getForks() { return forks; }
    public void setForks(Integer forks) { this.forks = forks; }
    public Integer getFollowers() { return followers; }
    public void setFollowers(Integer followers) { this.followers = followers; }
    public Map<String, Integer> getLanguages() { return languages; }
    public void setLanguages(Map<String, Integer> languages) { this.languages = languages; }
}