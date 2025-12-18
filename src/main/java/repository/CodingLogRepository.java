package repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.CodingLog;

@Repository
public interface CodingLogRepository extends JpaRepository<CodingLog, Long> {

    // Existing method from controller
    Optional<CodingLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    // *** CRITICAL ADDITION for CodingLogService ***
    List<CodingLog> findByUserId(Long userId);

    // Existing method from CodingLogService (if you kept it)
    List<CodingLog> findAllByOrderByLogDateDesc();
}