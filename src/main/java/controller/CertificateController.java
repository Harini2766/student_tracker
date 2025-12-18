package controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile; 

import dto.CertificateVerificationDTO;
import entity.Certificate;
import entity.User;
import repository.CertificateRepository;
import repository.UserRepository;
import service.CertificateService;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    // --- STUDENT ENDPOINTS ---
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCertificate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            certificateService.saveCertificate(user.getUserId(), name, file);
            return ResponseEntity.ok("Certificate uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getStudentCertificates(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(certificateService.getCertificatesByStudent(user.getUserId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching list: " + e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Long id) {
        try {
            Optional<Certificate> certOpt = certificateRepository.findById(id);
            if (certOpt.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

            String filename = certOpt.get().getFilePath();
            
            final String UPLOAD_DIR_BASE = "C:/StudentTrackerUploads/certificates/";
            
            Path filePath = Paths.get(UPLOAD_DIR_BASE + filename);
            
            // *** CRITICAL LOGGING: Print the path being accessed ***
            System.out.println("Attempting to access file at path: " + filePath.toAbsolutePath().toString());
            // ********************************************************
            
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {

                String contentType = "application/octet-stream";
                String lowerName = filename.toLowerCase();

                // Set correct content type
                if (lowerName.endsWith(".pdf")) { contentType = "application/pdf"; }
                else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) { contentType = "image/jpeg"; }
                else if (lowerName.endsWith(".png")) { contentType = "image/png"; }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        // Use 'inline' to view in browser, not download
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                // If file is not found, we print a clear message
                System.err.println("File NOT found on disk at path: " + filePath.toAbsolutePath().toString());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log the error for debugging
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable Long id, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            certificateService.deleteCertificate(user.getUserId(), id);
            return ResponseEntity.ok("Certificate deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Delete failed: " + e.getMessage());
        }
    }

    // ===========================================
    // *** STAFF ENDPOINTS FOR VERIFICATION ***
    // ===========================================

    /**
     * STAFF: GET /api/certificates/all 
     * Fetches ALL certificates (Pending, Verified, Rejected) for history/review.
     * This replaces the old /pending endpoint.
     */
    @GetMapping("/all") // FIX: Renamed from /pending to /all
    public ResponseEntity<List<CertificateVerificationDTO>> getAllCertificates() {
        // FIX: Use findAll() to fetch all certificates
        List<Certificate> allCerts = certificateRepository.findAll();

        List<CertificateVerificationDTO> dtos = allCerts.stream()
            .map(CertificateVerificationDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * STAFF: POST /api/certificates/verify/{id}
     */
    @PostMapping("/verify/{id}")
    public ResponseEntity<?> verifyCertificate(@PathVariable Long id) {
        try {
            certificateService.updateCertificateStatus(id, Certificate.Status.VERIFIED);
            return ResponseEntity.ok("Certificate verified successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * STAFF: POST /api/certificates/reject/{id}
     */
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectCertificate(@PathVariable Long id) {
        try {
            certificateService.updateCertificateStatus(id, Certificate.Status.REJECTED);
            return ResponseEntity.ok("Certificate rejected successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}