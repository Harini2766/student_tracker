package dto;

import java.util.Map;

// Used for Department-Wise Skill Growth Report
public class DepartmentGrowthDTO {
    private String departmentName;
    private Integer totalStudents;
    private Integer currentAverageGrade;
    private Map<String, Double> monthlyAverageGrade; // Key: Month/Year, Value: Average Score

    // Getters and Setters
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }

    public Integer getCurrentAverageGrade() { return currentAverageGrade; }
    public void setCurrentAverageGrade(Integer currentAverageGrade) { this.currentAverageGrade = currentAverageGrade; }

    public Map<String, Double> getMonthlyAverageGrade() { return monthlyAverageGrade; }
    public void setMonthlyAverageGrade(Map<String, Double> monthlyAverageGrade) { this.monthlyAverageGrade = monthlyAverageGrade; }
}