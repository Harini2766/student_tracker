package dto;

import java.util.List;

import entity.CodingLog;

public class DailyCodingDTO {
    private Integer todayCount;
    private Integer weekCount;
    private Integer monthCount;
    private List<CodingLog> history; // List of past entries

    // Getters and Setters
    public Integer getTodayCount() { return todayCount; }
    public void setTodayCount(Integer todayCount) { this.todayCount = todayCount; }
    public Integer getWeekCount() { return weekCount; }
    public void setWeekCount(Integer weekCount) { this.weekCount = weekCount; }
    public Integer getMonthCount() { return monthCount; }
    public void setMonthCount(Integer monthCount) { this.monthCount = monthCount; }
    public List<CodingLog> getHistory() { return history; }
    public void setHistory(List<CodingLog> history) { this.history = history; }
}