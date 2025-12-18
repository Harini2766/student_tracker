package repository;

import java.util.Optional; // ADD THIS IMPORT

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.StudentProfile;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    // Since the primary key is the userId, JpaRepository<StudentProfile, Long> provides findById.

    // FIX: Explicitly define method to search by the user ID field
    Optional<StudentProfile> findByUserId(Long userId);
}