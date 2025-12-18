package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entity.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    // FIX: Manually define the query to map Certificate -> User -> userId
    @Query("SELECT c FROM Certificate c WHERE c.user.userId = :userId")
    List<Certificate> findByUserId(@Param("userId") Long userId);

    // This one is fine
    List<Certificate> findByStatus(Certificate.Status status);
}