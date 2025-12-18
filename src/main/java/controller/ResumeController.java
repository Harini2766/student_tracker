package controller;

import java.io.File; // <-- NEW IMPORT
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Optional; // <-- Needed for Staff Download

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // <-- Needed for Staff Download
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Resume; // <-- Needed for Staff Download
import entity.User;
import repository.ResumeRepository; // <-- Needed for Staff Download
import repository.UserRepository;
import service.ResumeService;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired // <-- Inject ResumeRepository for Staff Download
    private ResumeRepository resumeRepository; 

    // *** CRITICAL FIX: Define the robust path base ***
    final String UPLOAD_DIR_BASE = "C:/StudentTrackerUploads/resumes/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElseThrow();

            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().body("Only PDF files are allowed.");
            }

            resumeService.saveResume(user.getUserId(), file);
            return ResponseEntity.ok("Resume uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public ResponseEntity<Resource> viewResume(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElseThrow();

            String filename = resumeService.getResumePath(user.getUserId());
            if (filename == null) {
				return ResponseEntity.notFound().build();
			}

            // CRITICAL FIX: Use the robust UPLOAD_DIR_BASE
            Path filePath = Paths.get(UPLOAD_DIR_BASE + filename); 
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        // Note: Using "inline" and the stored filename for better viewing
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log for debugging
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // *** STUDENT DELETE ENDPOINT ***
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteResume(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElseThrow();

            resumeService.deleteResume(user.getUserId());
            return ResponseEntity.ok("Resume deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Delete failed: " + e.getMessage());
        }
    }
    
    // *** STAFF DOWNLOAD ENDPOINT (Re-implemented for completeness) ***
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) {
        try {
            Optional<Resume> resumeOpt = resumeRepository.findById(id);
            if (resumeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String filename = resumeOpt.get().getFilePath();
            
            Path filePath = Paths.get(UPLOAD_DIR_BASE + filename);
            
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/pdf";
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        // Use 'inline' to view in browser
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}