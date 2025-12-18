package service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.LoginDTO;
import dto.RegisterDTO;
import entity.StudentProfile;
import entity.User;
import repository.StudentProfileRepository;
import repository.UserRepository;
import security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // NEW: Inject LeetCodeService to fetch data on login
    @Autowired
    private LeetCodeService leetCodeService;

    /**
     * Registers a new student and creates their associated profile.
     */
    @Transactional
    public User registerUser(RegisterDTO dto) {

        // 1. Validation
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Error: Username is already taken.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Error: Email already in use.");
        }

        // 2. Create User Entity
        User user = new User();
        user.setFullName(dto.getFullname());
        user.setDepartment(dto.getDepartment());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());

        // Use setPasswordHash
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        user.setDateOfBirth(dto.getDob());
        user.setRole(User.Role.STUDENT);
        user.setRegistrationDate(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // 3. Create StudentProfile entity
        StudentProfile profile = new StudentProfile();
        profile.setUser(savedUser);
        profile.setLeetcodeUsername(dto.getLeetcodeUsername());
        profile.setGithubLink(dto.getGithubLink());
        profile.setHackerrankLink(dto.getHackerrankUsername());
        profile.setLinkedinLink(dto.getLinkedinLink());

        studentProfileRepository.save(profile);

        return savedUser;
    }

    /**
     * Authenticates a user and returns a JWT token, Role, and UserID.
     */
    public Map<String, Object> loginUser(LoginDTO loginDTO) {

        User user = userRepository.findByUsername(loginDTO.getUsername())
            .orElseThrow(() -> new RuntimeException("Invalid username or password."));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password.");
        }

        // *** NEW FEATURE: AUTO-REFRESH LEETCODE DATA ON LOGIN (FIXED METHOD NAME) ***
        if (user.getRole() == User.Role.STUDENT) {
            try {
                // *** FIX APPLIED HERE: Calling the correct method name ***
                leetCodeService.refreshLeetCodeData(user.getUserId());
                System.out.println("Auto-refreshed LeetCode data for user: " + user.getUsername());
            } catch (Exception e) {
                // We just log the error and continue.
                // We do NOT want to block login if LeetCode is down or slow.
                System.err.println("Warning: Could not auto-refresh LeetCode data: " + e.getMessage());
            }
        }

        // Generate Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("userId", user.getUserId());

        return response;
    }
}