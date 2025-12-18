package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import entity.Certificate;
import entity.User;
import repository.CertificateRepository;
import repository.UserRepository;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    // *** FINAL PATH: External C: Drive path for automation and security bypass ***
    private final String UPLOAD_DIR = "C:/StudentTrackerUploads/certificates/";

    @Transactional
    public void saveCertificate(Long userId, String name, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found."));

        // 1. Create the upload directory if it doesn't exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 2. Generate a unique filename (User ID + Timestamp + Original Name)
        String fileName = userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        // 3. Save the file to disk
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Save metadata to Database
        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setName(name);
        certificate.setIssueDate(LocalDate.now());
        certificate.setFilePath(fileName); // We only save the filename, not the full path
        certificate.setStatus(Certificate.Status.PENDING);

        certificateRepository.save(certificate);
    }

    public List<Certificate> getCertificatesByStudent(Long userId) {
        return certificateRepository.findByUserId(userId);
    }

    /**
     * Updates a certificate status (Used for Staff Verify/Reject).
     */
    @Transactional
    public void updateCertificateStatus(Long certificateId, Certificate.Status status) {
        Certificate cert = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new RuntimeException("Certificate not found with ID: " + certificateId));

        // Update the status
        cert.setStatus(status);
        certificateRepository.save(cert);
    }

    @Transactional
    public void deleteCertificate(Long userId, Long certificateId) {
        Certificate cert = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new RuntimeException("Certificate not found with ID: " + certificateId));

        // 1. Security Check: Ensure the user owns this certificate
        if (!cert.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this certificate.");
        }

        // 2. Try to delete the file (But don't stop if it fails)
        try {
            Path filePath = Paths.get(UPLOAD_DIR + cert.getFilePath());
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                System.out.println("File was not found on disk, but deleting from DB anyway: " + cert.getFilePath());
            }
        } catch (IOException e) {
            // Log the error but ALLOW the transaction to continue so the UI updates
            System.err.println("Warning: Failed to delete file from disk (File might be in use or missing): " + e.getMessage());
        }

        // 3. Delete from Database
        certificateRepository.delete(cert);
    }
}