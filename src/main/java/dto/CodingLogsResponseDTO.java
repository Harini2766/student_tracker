package dto;

import java.time.LocalDate;
import java.util.List;

public class CodingLogsResponseDTO {

    private List<StudentLogEntry> studentLogs;
    private List<DailySummaryEntry> weeklySummary;

    // Getters and Setters for main DTO
    public List<StudentLogEntry> getStudentLogs() { return studentLogs; }
    public void setStudentLogs(List<StudentLogEntry> studentLogs) { this.studentLogs = studentLogs; }

    public List<DailySummaryEntry> getWeeklySummary() { return weeklySummary; }
    public void setWeeklySummary(List<DailySummaryEntry> weeklySummary) { this.weeklySummary = weeklySummary; }


    // --- Inner Class for Student Log Data ---
    public static class StudentLogEntry {
        private String name;
        private Integer today;
        private Integer week;
        private Integer currentStreak;
        private Integer longestStreak;
        private LocalDate lastLoggedDate; // *** Harmonized name for clarity ***

        // Default Constructor (Required for JSON)
        public StudentLogEntry() {}

        // Parameterized Constructor
        public StudentLogEntry(String name, Integer today, Integer week, Integer currentStreak, Integer longestStreak, LocalDate lastLoggedDate) {
            this.name = name;
            this.today = today;
            this.week = week;
            this.currentStreak = currentStreak;
            this.longestStreak = longestStreak;
            this.lastLoggedDate = lastLoggedDate;
        }

        // Getters and Setters
        public String getName() { return name; }
        public Integer getToday() { return today; }
        public Integer getWeek() { return week; }
        public Integer getCurrentStreak() { return currentStreak; }
        public Integer getLongestStreak() { return longestStreak; }
        public LocalDate getLastLoggedDate() { return lastLoggedDate; } 

        public void setName(String name) { this.name = name; }
        public void setToday(Integer today) { this.today = today; }
        public void setWeek(Integer week) { this.week = week; }
        public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }
        public void setLongestStreak(Integer longestStreak) { this.longestStreak = longestStreak; }
        public void setLastLoggedDate(LocalDate lastLoggedDate) { this.lastLoggedDate = lastLoggedDate; } 
    }

    // --- Inner Class for Weekly Summary Data ---
    public static class DailySummaryEntry {
        private String day; // *** Changed 'date' to 'day' to match service logic (Mon, Tue, etc.) ***
        private Integer totalSolved; // *** Changed 'solved' to 'totalSolved' to match service logic ***

        // Default Constructor (Required for JSON)
        public DailySummaryEntry() {}

        // Parameterized Constructor
        public DailySummaryEntry(String day, Integer totalSolved) {
            this.day = day;
            this.totalSolved = totalSolved;
        }

        // Getters and Setters
        public String getDay() { return day; }
        public Integer getTotalSolved() { return totalSolved; }
        public void setDay(String day) { this.day = day; }
        public void setTotalSolved(Integer totalSolved) { this.totalSolved = totalSolved; }
    }
}