package service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import dto.CodingLogsResponseDTO;
import dto.DailyCodingDTO;
import entity.CodingLog;
import entity.LeetCodeStats;
import entity.User;
import repository.CodingLogRepository;
import repository.LeetCodeStatsRepository;
import repository.StudentProfileRepository;
import repository.UserRepository;

@Service
public class CodingLogService {

    @Autowired private CodingLogRepository codingLogRepository;
    @Autowired private LeetCodeStatsRepository leetCodeStatsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StudentProfileRepository studentProfileRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    // --- STUDENT STATS METHOD (Existing - Minimal Change) ---
    public DailyCodingDTO getStudentStats(Long userId) {
        DailyCodingDTO dto = new DailyCodingDTO();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(7);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        List<CodingLog> mergedHistory = new ArrayList<>(
                codingLogRepository.findByUserId(userId)
            );

        int todaySum = 0;
        int weekSum = 0;
        int monthSum = 0;

        for (CodingLog log : mergedHistory) {
            if (log.getLogDate().isEqual(today)) {
				todaySum += log.getSolvedCount();
			}
            if (log.getLogDate().isAfter(startOfWeek)) {
				weekSum += log.getSolvedCount();
			}
            if (log.getLogDate().isAfter(startOfMonth.minusDays(1))) {
				monthSum += log.getSolvedCount();
			}
        }

        Optional<LeetCodeStats> lcStatsOpt = leetCodeStatsRepository.findByUserId(userId);

        if (lcStatsOpt.isPresent()) {
            String calendarJson = lcStatsOpt.get().getSubmissionCalendar();

            if (calendarJson != null && !calendarJson.trim().isEmpty() && !calendarJson.trim().equals("{}")) {
                try {
                    Map<String, Integer> submissionMap = objectMapper.readValue(calendarJson, Map.class);

                    for (Map.Entry<String, Integer> entry : submissionMap.entrySet()) {
                        try {
                            long timestamp = Long.parseLong(entry.getKey());
                            LocalDate subDate = Instant.ofEpochSecond(timestamp)
                                                       .atZone(DEFAULT_ZONE)
                                                       .toLocalDate();
                            int count = entry.getValue() != null ? entry.getValue() : 0;
                            if (count <= 0) {
								continue;
							}

                            if (subDate.isEqual(today)) {
								todaySum += count;
							}
                            if (subDate.isAfter(startOfWeek)) {
								weekSum += count;
							}
                            if (subDate.isAfter(startOfMonth.minusDays(1))) {
								monthSum += count;
							}

                            CodingLog lcLog = new CodingLog();
                            lcLog.setLogDate(subDate);
                            lcLog.setSolvedCount(count);
                            lcLog.setUserId(userId);
                            lcLog.setLogId(null);

                            mergedHistory.add(lcLog);
                        } catch (NumberFormatException e) {
                            System.err.println("Ignoring non-numeric timestamp: " + entry.getKey());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing LeetCode calendar for user " + userId + ": " + e.getMessage());
                }
            }
        }

        mergedHistory.sort(Comparator.comparing(CodingLog::getLogDate).reversed());

        dto.setTodayCount(todaySum);
        dto.setWeekCount(weekSum);
        dto.setMonthCount(monthSum);
        dto.setHistory(mergedHistory);

        return dto;
    }

    // --- LOG CODING METHOD (Optimized) ---
    @Transactional
    public void logDailyCoding(Long userId, Integer solvedCount) {
        if (solvedCount == null || solvedCount <= 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        Optional<CodingLog> existingLog = codingLogRepository.findByUserIdAndLogDate(userId, today);

        if (existingLog.isPresent()) {
            CodingLog log = existingLog.get();
            log.setSolvedCount(log.getSolvedCount() + solvedCount);
            codingLogRepository.save(log);
        } else {
            CodingLog newLog = new CodingLog();
            newLog.setUserId(userId);
            newLog.setSolvedCount(solvedCount);
            newLog.setLogDate(today);
            codingLogRepository.save(newLog);
        }
    }

    /**
     * CRITICAL FIX: Creates/Updates daily log entries in the database from external LeetCode data.
     */
    @Transactional
    public void updateDailyLogsFromLeetCode(Long userId, Map<String, Integer> submissionMap) {

        submissionMap.forEach((timestampStr, solvedCount) -> {

            if (solvedCount == null || solvedCount <= 0) {
                return; // Skip days with zero or null submissions
            }

            long timestampSeconds;
            try {
                timestampSeconds = Long.parseLong(timestampStr);
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid timestamp key: " + timestampStr);
                return;
            }

            LocalDate logDate = Instant.ofEpochSecond(timestampSeconds)
                                       .atZone(DEFAULT_ZONE)
                                       .toLocalDate();

            Optional<CodingLog> existingLog = codingLogRepository.findByUserIdAndLogDate(userId, logDate);

            if (existingLog.isPresent()) {
                CodingLog log = existingLog.get();
                log.setSolvedCount(solvedCount);
                codingLogRepository.save(log);
            } else {
                CodingLog newLog = new CodingLog();
                newLog.setUserId(userId);
                newLog.setLogDate(logDate);
                newLog.setSolvedCount(solvedCount);
                codingLogRepository.save(newLog);
            }
        });

        System.out.println("Finished saving logs for user: " + userId);
    }


 // --- STAFF REPORT METHOD (FINAL STABILITY FIX + DEBUGGING) ---
 public CodingLogsResponseDTO generateStaffReport() {
     CodingLogsResponseDTO response = new CodingLogsResponseDTO();
     List<CodingLogsResponseDTO.StudentLogEntry> studentEntries = new ArrayList<>();

     Map<LocalDate, Integer> weeklyClassTotals = initializeWeeklyTotalsMap();

     // 1. Fetch all students - Using the fixed query method
     List<User> allStudents = userRepository.findStudentUsersForReport();

     System.out.println("DEBUG: Found " + allStudents.size() + " students for the report.");


     for (User user : allStudents) {

         // Start a try-catch block for individual student processing
         try {
             Long userId = user.getUserId();
             String fullName = user.getFullName();

             System.out.println("Processing student: " + fullName + " (ID: " + userId + ")");

             // 2. Get All Logs (Manual + Merged LeetCode) for the student
             Map<LocalDate, Integer> dailySolvedMap = getDailySolvedMap(userId);

             // 3. Calculate Individual Student Stats (Including proper streak)
             StudentStats stats = calculateIndividualStats(dailySolvedMap);

             // 4. Aggregate Class Totals for the Chart
             aggregateClassTotals(dailySolvedMap, weeklyClassTotals);

             // 5. Create DTO entry for the student

             // *** CRITICAL FINAL FIX: Pass null if no log date is found ***
             LocalDate lastLogDate = stats.lastLogDate;

             studentEntries.add(new CodingLogsResponseDTO.StudentLogEntry(
                 fullName,
                 stats.todayCount,
                 stats.weekCount,
                 stats.currentStreak,
                 stats.longestStreak,
                 lastLogDate // Pass null if stats.lastLogDate is null
             ));
         } catch (Exception e) {
             // CRITICAL: Log the failure for a specific student and continue the loop
             System.err.println("CRITICAL ERROR: Failed to process logs for user ID: " + user.getUserId() + ". Error: " + e.getMessage());
             e.printStackTrace();
         }
     }

     response.setStudentLogs(studentEntries);
     response.setWeeklySummary(convertWeeklyTotalsToDTO(weeklyClassTotals));

     return response;
 }
    // --- PRIVATE HELPER CLASSES AND METHODS ---

    /** Private Class for Stats Calculation */
    private static class StudentStats {
        public int todayCount = 0;
        public int weekCount = 0;
        public int currentStreak = 0;
        public int longestStreak = 0;
        public LocalDate lastLogDate = null;
    }

    /** Initializes a map for the last 7 days with 0 problems solved. */
    private Map<LocalDate, Integer> initializeWeeklyTotalsMap() {
        Map<LocalDate, Integer> map = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            map.put(today.minusDays(i), 0);
        }
        return map;
    }

    /** Converts the aggregated class totals map into the required DTO list for Chart.js. */
    private List<CodingLogsResponseDTO.DailySummaryEntry> convertWeeklyTotalsToDTO(Map<LocalDate, Integer> totalsMap) {
        return totalsMap.entrySet().stream()
            .map(entry -> {
                String dayName = entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                return new CodingLogsResponseDTO.DailySummaryEntry(dayName, entry.getValue());
            })
            .collect(Collectors.toList());
    }

    /** Aggregates student daily counts into the class weekly totals map. */
    private void aggregateClassTotals(Map<LocalDate, Integer> dailySolvedMap, Map<LocalDate, Integer> weeklyClassTotals) {
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        dailySolvedMap.forEach((date, count) -> {
            if (date.isAfter(startOfWeek) && weeklyClassTotals.containsKey(date)) {
                weeklyClassTotals.put(date, weeklyClassTotals.get(date) + count);
            }
        });
    }

    /** Fetches and merges Manual Logs and LeetCode Submission Calendar into a single Map<Date, Count>. */
    private Map<LocalDate, Integer> getDailySolvedMap(Long userId) {
        Map<LocalDate, Integer> map = new HashMap<>();

        // 1. Get Manual Logs
        List<CodingLog> manualLogs = codingLogRepository.findByUserId(userId);
        manualLogs.forEach(log -> map.put(log.getLogDate(), map.getOrDefault(log.getLogDate(), 0) + log.getSolvedCount()));

        // 2. Merge LeetCode Data (With Final Robustness Check)
        // The try-catch block now wraps the entire LeetCode data merging section
        try {
            Optional<LeetCodeStats> lcStatsOpt = leetCodeStatsRepository.findByUserId(userId);

            if (lcStatsOpt.isPresent()) {
                LeetCodeStats stats = lcStatsOpt.get();
                String calendarJson = stats.getSubmissionCalendar();

                // CRITICAL ROBUSTNESS CHECK: Ensures the string is valid JSON before parsing
                if (calendarJson != null && !calendarJson.trim().isEmpty() && !calendarJson.trim().equals("{}")) {
                    Map<String, Integer> submissionMap = objectMapper.readValue(calendarJson, Map.class);
                    submissionMap.forEach((key, count) -> {
                        // Ensure count is not null and is valid
                        if (count != null && count > 0) {
                            try {
                                long timestamp = Long.parseLong(key);
                                LocalDate subDate = Instant.ofEpochSecond(timestamp)
                                                           .atZone(DEFAULT_ZONE)
                                                           .toLocalDate();
                                // Merge: Add the LeetCode count to the existing manual count for the date
                                map.put(subDate, map.getOrDefault(subDate, 0) + count);
                            } catch (NumberFormatException e) {
                                 // Ignore bad timestamps
                            }
                        }
                    });
                }
            } else {
                 // Debugging log for missing data, this is not a crash, but information
                 System.out.println("DEBUG: LeetCodeStats not found for user " + userId + ". Returning only manual logs.");
            }
        } catch (Exception e) {
            // CRITICAL CATCH: Log the error but continue execution, returning the map with only manual logs
            System.err.println("JSON/Parsing error in LeetCode calendar for user " + userId + ". Returning only manual logs. Error: " + e.getMessage());
            // This guarantees the method returns successfully.
        }

        return map;
    }

    /** Calculates streak metrics from the daily solved map. */
    private StudentStats calculateIndividualStats(Map<LocalDate, Integer> dailySolvedMap) {
        StudentStats stats = new StudentStats();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<LocalDate> activeDays = dailySolvedMap.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .map(Map.Entry::getKey)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        if (!activeDays.isEmpty()) {
            stats.lastLogDate = activeDays.get(0);
        }

        // --- Weekly/Today Count ---
        LocalDate startOfWeek = today.minusDays(7);
        dailySolvedMap.forEach((d, count) -> {
            if (d.isEqual(today)) {
				stats.todayCount = count;
			}
            if (d.isAfter(startOfWeek)) {
				stats.weekCount += count;
			}
        });

        // --- Streak Calculation (Optimized and Corrected Logic) ---
        int currentStreak = 0;

        if (dailySolvedMap.getOrDefault(today, 0) > 0) {
            currentStreak = 1;
        } else if (dailySolvedMap.getOrDefault(yesterday, 0) > 0) {
            currentStreak = 1;
        }

        LocalDate streakDate = today.minusDays(2);

        if (currentStreak > 0) {
            for (int i = 0; i < 365; i++) {
                int solvedCount = dailySolvedMap.getOrDefault(streakDate, 0);

                if (solvedCount > 0) {
                    currentStreak++;
                    streakDate = streakDate.minusDays(1);
                } else {
                    break;
                }
            }
        }

        stats.currentStreak = currentStreak;
        stats.longestStreak = currentStreak;

        return stats;
    }
}