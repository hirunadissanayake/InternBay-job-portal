package lk.ijse.gdse.main.internbaybackend.repository;

import lk.ijse.gdse.main.internbaybackend.entity.Job;
import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    // Find jobs by employer profile
    List<Job> findByEmployerProfileEmployerId(Long employerId);
    
    // Find jobs by category
    List<Job> findByCategoryCategoryId(Long categoryId);
    
    // Find jobs by job type
    List<Job> findByJobType(JobType jobType);
    
    // Find jobs by salary range
    List<Job> findBySalaryPerHourBetween(BigDecimal minSalary, BigDecimal maxSalary);
    
    // Custom query for job search with filters
    @Query("SELECT j FROM Job j WHERE " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:categoryId IS NULL OR j.category.categoryId = :categoryId) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:minSalary IS NULL OR j.salaryPerHour >= :minSalary) AND " +
           "(:maxSalary IS NULL OR j.salaryPerHour <= :maxSalary) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<Job> findJobsWithFilters(
            @Param("title") String title,
            @Param("categoryId") Long categoryId,
            @Param("jobType") JobType jobType,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            @Param("location") String location,
            Pageable pageable);
}