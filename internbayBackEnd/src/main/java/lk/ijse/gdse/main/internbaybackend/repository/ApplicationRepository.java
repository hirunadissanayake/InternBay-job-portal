// Fixed ApplicationRepository.java
package lk.ijse.gdse.main.internbaybackend.repository;

import lk.ijse.gdse.main.internbaybackend.entity.Application;
import lk.ijse.gdse.main.internbaybackend.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Check if user already applied for a job
    boolean existsByUserUserIdAndJobJobId(Long userId, Long jobId);

    // Get user's applications - Fixed: Use Long directly
    @Query("SELECT a FROM Application a WHERE a.user.userId = :userId ORDER BY a.appliedAt DESC")
    Page<Application> findByUserUserIdOrderByAppliedAtDesc(@Param("userId") Long userId, Pageable pageable);

    // Get applications for a specific job
    @Query("SELECT a FROM Application a WHERE a.job.jobId = :jobId ORDER BY a.appliedAt DESC")
    Page<Application> findByJobJobIdOrderByAppliedAtDesc(@Param("jobId") Long jobId, Pageable pageable);

    // Get all applications for employer's jobs - Fixed query
    @Query("SELECT a FROM Application a JOIN a.job j JOIN j.employerProfile ep WHERE ep.user.userId = :employerId ORDER BY a.appliedAt DESC")
    Page<Application> findByJobEmployerProfileUserUserIdOrderByAppliedAtDesc(@Param("employerId") Long employerId, Pageable pageable);

    // Get applications by employer and status - Fixed query
    @Query("SELECT a FROM Application a JOIN a.job j JOIN j.employerProfile ep WHERE ep.user.userId = :employerId AND a.status = :status ORDER BY a.appliedAt DESC")
    Page<Application> findByJobEmployerProfileUserUserIdAndStatusOrderByAppliedAtDesc(
            @Param("employerId") Long employerId,
            @Param("status") ApplicationStatus status,
            Pageable pageable);

    // Get all applications for employer (for statistics) - Fixed query
    @Query("SELECT a FROM Application a JOIN a.job j JOIN j.employerProfile ep WHERE ep.user.userId = :employerId")
    List<Application> findByJobEmployerProfileUserUserId(@Param("employerId") Long employerId);
}