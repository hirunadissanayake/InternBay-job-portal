// JobService.java
package lk.ijse.gdse.main.internbaybackend.service;

import lk.ijse.gdse.main.internbaybackend.dto.JobCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.JobResponseDTO;
import lk.ijse.gdse.main.internbaybackend.dto.JobStatsDTO;
import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface JobService {
    int createJob(String employerEmail, JobCreateDTO jobCreateDTO);
    JobResponseDTO getJobById(Long jobId);
    Page<JobResponseDTO> searchJobs(String title, String location, Long categoryId,
                                    List<JobType> jobTypes, BigDecimal minSalary,
                                    BigDecimal maxSalary, Pageable pageable);
    Page<JobResponseDTO> getJobsByEmployer(String employerEmail, Long employerId, Pageable pageable);
    int updateJob(String employerEmail, Long jobId, JobCreateDTO jobUpdateDTO);
    int deleteJob(String employerEmail, Long jobId);
    int toggleJobStatus(String employerEmail, Long jobId);
    JobStatsDTO getEmployerJobStats(String employerEmail, Long employerId);
}