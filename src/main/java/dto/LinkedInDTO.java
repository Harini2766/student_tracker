package dto;

public class LinkedInDTO {
    private String username;
    private String profileLink;
    private Integer connections;
    private Integer followers;
    private Integer profileViews; // Placeholder for now
    private Integer postImpressions; // Placeholder for now

    private String profileStrength;
    private int strengthScore; // 0 to 100 for the progress bar

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProfileLink() { return profileLink; }
    public void setProfileLink(String profileLink) { this.profileLink = profileLink; }

    public Integer getConnections() { return connections; }
    public void setConnections(Integer connections) { this.connections = connections; }

    public Integer getFollowers() { return followers; }
    public void setFollowers(Integer followers) { this.followers = followers; }

    public Integer getProfileViews() { return profileViews; }
    public void setProfileViews(Integer profileViews) { this.profileViews = profileViews; }

    public Integer getPostImpressions() { return postImpressions; }
    public void setPostImpressions(Integer postImpressions) { this.postImpressions = postImpressions; }

    public String getProfileStrength() { return profileStrength; }
    public void setProfileStrength(String profileStrength) { this.profileStrength = profileStrength; }

    public int getStrengthScore() { return strengthScore; }
    public void setStrengthScore(int strengthScore) { this.strengthScore = strengthScore; }
}