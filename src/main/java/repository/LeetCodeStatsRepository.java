package repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.LeetCodeStats;

@Repository
public interface LeetCodeStatsRepository extends JpaRepository<LeetCodeStats, Long> {

    // Finds the latest or primary stats record for a user
    Optional<LeetCodeStats> findByUserId(Long userId);
}