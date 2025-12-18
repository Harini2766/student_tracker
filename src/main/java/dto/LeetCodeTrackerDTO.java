package dto;

import java.util.List;

public class LeetCodeTrackerDTO {
    private Long id;
    private String name;
    private String dept;
    private Integer total;
    private Integer easy;
    private Integer med;
    private Integer hard;
    private Integer rating;
    private Integer today;
    private List<Integer> activity;

    // --- Constructors ---
    public LeetCodeTrackerDTO() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public Integer getEasy() { return easy; }
    public void setEasy(Integer easy) { this.easy = easy; }

    public Integer getMed() { return med; }
    public void setMed(Integer med) { this.med = med; }

    public Integer getHard() { return hard; }
    public void setHard(Integer hard) { this.hard = hard; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Integer getToday() { return today; }
    public void setToday(Integer today) { this.today = today; }

    public List<Integer> getActivity() { return activity; }
    public void setActivity(List<Integer> activity) { this.activity = activity; }
}