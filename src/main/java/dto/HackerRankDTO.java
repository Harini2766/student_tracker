package dto;

import java.util.ArrayList;
import java.util.List;

public class HackerRankDTO {
    private String username;
    private int badges;
    private int solved;
    private int certificates;

    // *** NEW CLASS FOR SKILL BARS ***
    public static class Skill {
        public String name;
        public int percent;
        public String color;

        public Skill(String name, int percent, String color) {
            this.name = name;
            this.percent = percent;
            this.color = color;
        }
    }

    private List<Skill> skills = new ArrayList<>();
    private List<String> topBadges = new ArrayList<>();

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getBadges() { return badges; }
    public void setBadges(int badges) { this.badges = badges; }
    public int getSolved() { return solved; }
    public void setSolved(int solved) { this.solved = solved; }
    public int getCertificates() { return certificates; }
    public void setCertificates(int certificates) { this.certificates = certificates; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public List<String> getTopBadges() { return topBadges; }
    public void setTopBadges(List<String> topBadges) { this.topBadges = topBadges; }
}