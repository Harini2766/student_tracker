package dto;

import java.util.List;

public class GitHubDTO {
    private String username;
    private Integer publicRepos;
    private Integer followers;
    private Integer following;
    private Integer commitsThisWeek; // Calculated from events
    private List<String> topLanguages; // e.g., ["Java", "Python", "HTML"]

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getPublicRepos() { return publicRepos; }
    public void setPublicRepos(Integer publicRepos) { this.publicRepos = publicRepos; }
    public Integer getFollowers() { return followers; }
    public void setFollowers(Integer followers) { this.followers = followers; }
    public Integer getFollowing() { return following; }
    public void setFollowing(Integer following) { this.following = following; }
    public Integer getCommitsThisWeek() { return commitsThisWeek; }
    public void setCommitsThisWeek(Integer commitsThisWeek) { this.commitsThisWeek = commitsThisWeek; }
    public List<String> getTopLanguages() { return topLanguages; }
    public void setTopLanguages(List<String> topLanguages) { this.topLanguages = topLanguages; }
}