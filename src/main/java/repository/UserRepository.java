package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used by AuthService for login
    Optional<User> findByUsername(String username);

    // Used by AuthService for registration validation
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // Existing: Used by StaffService for student overview (by Enum)
    // We keep this method for other parts of the application that rely on it.
    List<User> findByRole(User.Role role);

    // REMOVED: List<User> findAllByRole(String role); - This was redundant and problematic.

    // CRITICAL FIX: Explicit JPA Query for the Staff Report
    // This bypasses potential MySQL ENUM mapping issues and ensures the list of students is retrieved.
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT'")
    List<User> findStudentUsersForReport();
}