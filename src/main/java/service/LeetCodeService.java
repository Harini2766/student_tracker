package service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.LeetCodeDTO;
import entity.LeetCodeStats;
import entity.StudentProfile;
import repository.LeetCodeStatsRepository;
import repository.StudentProfileRepository;

@Service
public class LeetCodeService {

    @Autowired
    private LeetCodeStatsRepository leetCodeStatsRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CodingLogService codingLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LeetCodeDTO getStatsDTO(Long userId) {

        LeetCodeStats stats = leetCodeStatsRepository.findByUserId(userId)
                .orElse(new LeetCodeStats());

        LeetCodeDTO dto = new LeetCodeDTO();
        dto.setTotalSolved(stats.getTotalSolved() != null ? stats.getTotalSolved() : 0);
        dto.setEasySolved(stats.getEasySolved() != null ? stats.getEasySolved() : 0);
        dto.setMediumSolved(stats.getMediumSolved() != null ? stats.getMediumSolved() : 0);
        dto.setHardSolved(stats.getHardSolved() != null ? stats.getHardSolved() : 0);
        dto.setRanking(stats.getContestRating() != null ? stats.getContestRating() : 0);
        dto.setAcceptanceRate("N/A"); // LeetCode does NOT expose exact acceptance rate

        // ✅ CORRECT: Calculate Total Active Days from submissionCalendar
        dto.setContributionPoints(calculateActiveDays(stats.getSubmissionCalendar()));

        dto.setSubmissionCalendar(stats.getSubmissionCalendar());
        return dto;
    }

    /**
     * Counts number of days with at least one submission
     */
    private int calculateActiveDays(String calendarJson) {
        if (calendarJson == null || calendarJson.isEmpty()) {
            return 0;
        }

        try {
            Map<String, Integer> submissionMap = objectMapper.readValue(
                    calendarJson,
                    new TypeReference<Map<String, Integer>>() {}
            );

            int activeDays = 0;
            for (Integer count : submissionMap.values()) {
                if (count != null && count > 0) {
                    activeDays++;
                }
            }
            return activeDays;

        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public void refreshLeetCodeData(Long userId) {

        StudentProfile profile = studentProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found."));

        String username = profile.getLeetcodeUsername();
        if (username == null || username.isEmpty()) {
            return;
        }

        try {
            LeetCodeStats fetchedStats = fetchFromLeetCodeApi(username);

            LeetCodeStats existingStats =
                    leetCodeStatsRepository.findByUserId(userId).orElse(null);

            if (existingStats != null) {
                existingStats.setTotalSolved(fetchedStats.getTotalSolved());
                existingStats.setEasySolved(fetchedStats.getEasySolved());
                existingStats.setMediumSolved(fetchedStats.getMediumSolved());
                existingStats.setHardSolved(fetchedStats.getHardSolved());
                existingStats.setContestRating(fetchedStats.getContestRating());
                existingStats.setSubmissionCalendar(fetchedStats.getSubmissionCalendar());
                existingStats.setLastUpdated(java.time.LocalDateTime.now());
                leetCodeStatsRepository.save(existingStats);
            } else {
                fetchedStats.setUserId(userId);
                fetchedStats.setLastUpdated(java.time.LocalDateTime.now());
                leetCodeStatsRepository.save(fetchedStats);
            }

            profile.setLeetcodeSolved(fetchedStats.getTotalSolved());
            studentProfileRepository.save(profile);

            if (fetchedStats.getSubmissionCalendar() != null) {
                Map<String, Integer> submissionMap = objectMapper.readValue(
                        fetchedStats.getSubmissionCalendar(),
                        new TypeReference<Map<String, Integer>>() {}
                );
                codingLogService.updateDailyLogsFromLeetCode(userId, submissionMap);
            }

        } catch (IOException e) {
            System.err.println("LeetCode Sync Failed: " + e.getMessage());
        }
    }

    private LeetCodeStats fetchFromLeetCodeApi(String username) {

        String url = "https://leetcode.com/graphql";

        String query = String.format(
            "{\"query\":\"query userStats($username: String!) { matchedUser(username: $username) { submissionCalendar submitStats: submitStatsGlobal { acSubmissionNum { difficulty count } } profile { ranking } } }\",\"variables\":{\"username\":\"%s\"}}",
            username
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(query, headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> body = response.getBody();
        Map<String, Object> matchedUser =
                (Map<String, Object>) ((Map<String, Object>) body.get("data")).get("matchedUser");

        String calendar = (String) matchedUser.get("submissionCalendar");

        List<Map<String, Object>> acSubmissionNum =
                (List<Map<String, Object>>) ((Map<String, Object>) matchedUser.get("submitStats"))
                        .get("acSubmissionNum");

        Map<String, Object> profile = (Map<String, Object>) matchedUser.get("profile");

        int easy = 0, medium = 0, hard = 0, total = 0;
        for (Map<String, Object> item : acSubmissionNum) {
            String diff = (String) item.get("difficulty");
            int count = (Integer) item.get("count");
            if ("Easy".equals(diff)) easy = count;
            else if ("Medium".equals(diff)) medium = count;
            else if ("Hard".equals(diff)) hard = count;
            else if ("All".equals(diff)) total = count;
        }

        LeetCodeStats stats = new LeetCodeStats();
        stats.setEasySolved(easy);
        stats.setMediumSolved(medium);
        stats.setHardSolved(hard);
        stats.setTotalSolved(total);
        stats.setContestRating((Integer) profile.get("ranking"));
        stats.setSubmissionCalendar(calendar);

        return stats;
    }
}
