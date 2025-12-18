package scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import entity.User;
import repository.UserRepository;
import service.GitHubService;
import service.HackerRankService;
import service.LeetCodeService;

@Component
public class DataSyncScheduler {

    @Autowired private UserRepository userRepository;
    @Autowired private LeetCodeService leetCodeService;
    @Autowired private GitHubService gitHubService;
    @Autowired private HackerRankService hackerRankService;

    // Runs every day at Midnight (00:00:00)
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoSyncAllStudents() {
        System.out.println("⏰ [Auto-Sync] Started 24h refresh...");

        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            try {
                Long uid = user.getUserId();

                // *** FIX APPLIED HERE: Using the correct method name ***
                leetCodeService.refreshLeetCodeData(uid);

                gitHubService.refreshGitHubData(uid);
                hackerRankService.refreshHackerRankData(uid);
            } catch (Exception e) {
                // Ignore errors so one failure doesn't stop the whole loop
                System.err.println("Skipping user " + user.getUsername() + " during scheduled sync.");
            }
        }
        System.out.println("✅ [Auto-Sync] Daily update complete.");
    }
}