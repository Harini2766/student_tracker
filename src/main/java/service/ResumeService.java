package service;

import java.io.File; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import entity.Resume;
import entity.StudentProfile;
import entity.User;
import repository.ResumeRepository;
import repository.StudentProfileRepository;
import repository.UserRepository;

@Service
public class ResumeService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    // *** FINAL PATH: External C: Drive path for automation and security bypass ***
    private final String UPLOAD_DIR = "C:/StudentTrackerUploads/resumes/";

    /**
     * STUDENT: Saves the new resume file, updates StudentProfile, AND creates a Resume entity for staff review.
     */
    @Transactional
    public void saveResume(Long userId, MultipartFile file) throws IOException {

        StudentProfile profile = studentProfileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        User user = profile.getUser();
        if (user == null) {
            throw new RuntimeException("User object not linked to StudentProfile.");
        }

        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
			directory.mkdirs();
		}

        // 1. Delete old resume file on disk associated with the StudentProfile path (Cleanup)
        if (profile.getResumePath() != null) {
            try {
                // Use the CORRECT UPLOAD_DIR for deletion
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + profile.getResumePath()));
            } catch (IOException ignored) {}
        }

        // 2. Save New File
        String fileName = userId + "_Resume_" + System.currentTimeMillis() + ".pdf";
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 3. Update StudentProfile (Tracks the current resume path/status for the student's view)
        profile.setResumePath(fileName);
        profile.setResumeStatus("Pending Review");
        studentProfileRepository.save(profile);

        // 4. CRITICAL FIX: Create and Save the Resume Entity for Staff Review LISTING
        Resume newResume = new Resume();
        newResume.setUser(user);
        newResume.setFilePath(fileName);
        // reviewScore is left NULL here, making it 'pending'.
        resumeRepository.save(newResume);
    }

    /**
     * STUDENT: Gets the filename of the current resume for viewing.
     */
    public String getResumePath(Long userId) {
        StudentProfile profile = studentProfileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return profile.getResumePath();
    }

    // *** NEW STAFF METHOD 1: Get Pending Resumes (Used by the Resume Review page) ***
    public List<Resume> getPendingResumes() {
        return resumeRepository.findByReviewScoreIsNull();
    }

    // *** NEW STAFF METHOD 2: Get ALL Resumes (Used by the new All Resumes History/Overview) ***
    public List<Resume> getAllResumes() {
        return resumeRepository.findAllByOrderByUploadDateDesc();
    }

    /**
     * STAFF: Action to approve or reject a resume entry.
     */
    @Transactional
    public void reviewResume(Long resumeId, String action, String feedback) {
        // *** CRITICAL FIX: Use resumeRepository to find the Resume entity ***
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new RuntimeException("Resume not found with ID: " + resumeId));

        if ("approve".equalsIgnoreCase(action)) {
            resume.setReviewScore(100);
            resume.setReviewFeedback("Approved. Staff Score: 100");

            // Update the StudentProfile status for dashboard display
            StudentProfile profile = studentProfileRepository.findById(resume.getUser().getUserId())
                 .orElseThrow(() -> new RuntimeException("Profile not found"));
            profile.setResumeStatus("Reviewed - Approved (100)");
            studentProfileRepository.save(profile);

        } else if ("reject".equalsIgnoreCase(action)) {
            resume.setReviewScore(0);
            resume.setReviewFeedback(feedback != null ? feedback : "Rejected: Needs major revision.");

            // Update the StudentProfile status
            StudentProfile profile = studentProfileRepository.findById(resume.getUser().getUserId())
                 .orElseThrow(() -> new RuntimeException("Profile not found"));
            profile.setResumeStatus("Reviewed - Rejected (0)");
            studentProfileRepository.save(profile);

        } else {
            throw new IllegalArgumentException("Invalid review action.");
        }

        resumeRepository.save(resume);
    }

    /**
     * STUDENT: Deletes the resume file and clears the profile data AND the staff review entries (CRITICAL FIX).
     */
    @Transactional
    public void deleteResume(Long userId) {
        StudentProfile profile = studentProfileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getResumePath() != null) {
            // 1. Delete file from disk
            try {
                // Use the CORRECT UPLOAD_DIR for deletion
                Path filePath = Paths.get(UPLOAD_DIR + profile.getResumePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete resume file: " + e.getMessage());
            }

            // 2. CLEAR DATABASE ENTRIES FOR STAFF REVIEW (CRITICAL FIX)
            // Finds all resume entries associated with the user and deletes them.
            List<Resume> userResumes = resumeRepository.findByUserIdOrderByUploadDateDesc(userId);
            resumeRepository.deleteAll(userResumes);


            // 3. Clear Database Info
            profile.setResumePath(null);
            profile.setResumeStatus("Not Uploaded");
            studentProfileRepository.save(profile);
        }
    }
}