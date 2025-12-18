package repository;

import java.util.Optional; // ADD THIS IMPORT

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.HackerRankStats;

@Repository
public interface HackerRankStatsRepository extends JpaRepository<HackerRankStats, Long> {
    
    // FIX: Explicitly define method to search by the user ID field
    Optional<HackerRankStats> findByUserId(Long userId);
}