package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // Find the single, most recently uploaded resume for a student.
    // Assuming 'uploadDate' is the field to determine the latest.
    Optional<Resume> findTopByUserIdOrderByUploadDateDesc(Long userId);

    // Find all resumes (for history view)
    List<Resume> findByUserIdOrderByUploadDateDesc(Long userId);

    // Find resumes that have not been reviewed yet
    List<Resume> findByReviewScoreIsNull();

    // *** NEW: Find all resumes for staff history/overview ***
    List<Resume> findAllByOrderByUploadDateDesc();
}