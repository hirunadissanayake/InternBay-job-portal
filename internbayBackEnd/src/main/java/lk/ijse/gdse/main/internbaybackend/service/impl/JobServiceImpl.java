// JobServiceImpl.java
package lk.ijse.gdse.main.internbaybackend.service.impl;

import lk.ijse.gdse.main.internbaybackend.dto.JobCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.JobResponseDTO;
import lk.ijse.gdse.main.internbaybackend.dto.JobStatsDTO;
import lk.ijse.gdse.main.internbaybackend.entity.*;
import lk.ijse.gdse.main.internbaybackend.repository.*;
import lk.ijse.gdse.main.internbaybackend.service.JobService;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final CategoryRepository categoryRepository;
    //private final ApplicationRepository applicationRepository;

    public JobServiceImpl(JobRepository jobRepository,
                          UserRepository userRepository,
                          EmployerProfileRepository employerProfileRepository,
                          CategoryRepository categoryRepository
                          /*ApplicationRepository applicationRepository*/) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.categoryRepository = categoryRepository;
        /*this.applicationRepository = applicationRepository;*/
    }

    @Override
    public int createJob(String employerEmail, JobCreateDTO jobCreateDTO) {
        try {
            User employer = userRepository.findByEmail(employerEmail);
            if (employer == null) {
                return StatusList.Not_Found;
            }

            Optional<EmployerProfile> employerProfileOpt = employerProfileRepository.findByUser(employer);
            if (employerProfileOpt.isEmpty()) {
                return StatusList.Not_Found;
            }

            EmployerProfile employerProfile = employerProfileOpt.get();

            Category category = categoryRepository.findById(jobCreateDTO.getCategoryId()).orElse(null);
            if (category == null) {
                return StatusList.Bad_Request;
            }

            Job job = Job.builder()
                    .employerProfile(employerProfile)
                    .category(category)
                    .title(jobCreateDTO.getTitle())
                    .location(jobCreateDTO.getLocation())
                    .jobType(jobCreateDTO.getJobType())
                    .salaryPerHour(jobCreateDTO.getSalaryPerHour())
                    .jobOverview(jobCreateDTO.getJobOverview())
                    .responsibilities(jobCreateDTO.getResponsibilities())
                    .requirements(jobCreateDTO.getRequirements())
                    .datePosted(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            jobRepository.save(job);
            return StatusList.Created;

        } catch (Exception e) {
            e.printStackTrace();
            return StatusList.Internal_Server_Error;
        }
    }

    @Override
    public JobResponseDTO getJobById(Long jobId) {
        try {
            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return null;
            }

            Job job = jobOpt.get();
            return convertToResponseDTO(job);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<JobResponseDTO> searchJobs(String title, String location, Long categoryId,
                                           List<JobType> jobTypes, BigDecimal minSalary,
                                           BigDecimal maxSalary, Pageable pageable) {
        try {
            Page<Job> jobs = jobRepository.findJobsWithFilters(
                    title, categoryId, jobTypes != null ? jobTypes.get(0) : null,
                    minSalary, maxSalary, location, pageable);

            List<JobResponseDTO> jobDTOs = jobs.getContent().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return new PageImpl<>(jobDTOs, pageable, jobs.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<JobResponseDTO> getJobsByEmployer(String employerEmail, Long employerId, Pageable pageable) {
        try {
            User employer = userRepository.findByEmail(employerEmail);
            if (employer == null) {
                return Page.empty(pageable);
            }

            Optional<EmployerProfile> employerProfileOpt = employerProfileRepository.findByUser(employer);
            if (employerProfileOpt.isEmpty() || !employerProfileOpt.get().getEmployerId().equals(employerId)) {
                return Page.empty(pageable);
            }

            List<Job> jobs = jobRepository.findByEmployerProfileEmployerId(employerId);
            List<JobResponseDTO> jobDTOs = jobs.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            // Manual pagination since we're not using repository pagination here
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), jobDTOs.size());
            List<JobResponseDTO> pageContent = jobDTOs.subList(start, end);

            return new PageImpl<>(pageContent, pageable, jobDTOs.size());

        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }

    @Override
    public int updateJob(String employerEmail, Long jobId, JobCreateDTO jobUpdateDTO) {
        try {
            User employer = userRepository.findByEmail(employerEmail);
            if (employer == null) {
                return StatusList.Not_Found;
            }

            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return StatusList.Not_Found;
            }

            Job job = jobOpt.get();

            // Check if the employer owns this job
            if (!job.getEmployerProfile().getUser().getEmail().equals(employerEmail)) {
                return StatusList.Unauthorized;
            }

            // Update job fields
            job.setTitle(jobUpdateDTO.getTitle());
            job.setLocation(jobUpdateDTO.getLocation());
            job.setJobType(jobUpdateDTO.getJobType());
            job.setSalaryPerHour(jobUpdateDTO.getSalaryPerHour());
            job.setJobOverview(jobUpdateDTO.getJobOverview());

            if (jobUpdateDTO.getResponsibilities() != null) {
                job.setResponsibilities(jobUpdateDTO.getResponsibilities());
            }

            if (jobUpdateDTO.getRequirements() != null) {
                job.setRequirements(jobUpdateDTO.getRequirements());
            }

            if (jobUpdateDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(jobUpdateDTO.getCategoryId()).orElse(null);
                if (category != null) {
                    job.setCategory(category);
                }
            }

            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
            return StatusList.OK;

        } catch (Exception e) {
            e.printStackTrace();
            return StatusList.Internal_Server_Error;
        }
    }

    @Override
    public int deleteJob(String employerEmail, Long jobId) {
        try {
            User employer = userRepository.findByEmail(employerEmail);
            if (employer == null) {
                return StatusList.Not_Found;
            }

            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return StatusList.Not_Found;
            }

            Job job = jobOpt.get();

            // Check if the employer owns this job
            if (!job.getEmployerProfile().getUser().getEmail().equals(employerEmail)) {
                return StatusList.Unauthorized;
            }

            jobRepository.delete(job);
            return StatusList.OK;

        } catch (Exception e) {
            e.printStackTrace();
            return StatusList.Internal_Server_Error;
        }
    }

    @Override
    public int toggleJobStatus(String employerEmail, Long jobId) {
        try {
            User employer = userRepository.findByEmail(employerEmail);
            if (employer == null) {
                return StatusList.Not_Found;
            }

            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return StatusList.Not_Found;
            }

            Job job = jobOpt.get();

            // Check if the employer owns this job
            if (!job.getEmployerProfile().getUser().getEmail().equals(employerEmail)) {
                return StatusList.Unauthorized;
            }

            // Toggle job status logic would go here
            // For now, just update the updatedAt timestamp
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
            return StatusList.OK;

        } catch (Exception e) {
            e.printStackTrace();
            return StatusList.Internal_Server_Error;
        }
    }

    @Override
    public JobStatsDTO getEmployerJobStats(String employerEmail, Long employerId) {
        try {
            User employer = userRepository.findByEmail(employerEmail);
            if (employer == null) {
                return JobStatsDTO.builder().build();
            }

            Optional<EmployerProfile> employerProfileOpt = employerProfileRepository.findByUser(employer);
            if (employerProfileOpt.isEmpty() || !employerProfileOpt.get().getEmployerId().equals(employerId)) {
                return JobStatsDTO.builder().build();
            }

            List<Job> jobs = jobRepository.findByEmployerProfileEmployerId(employerId);

            int totalJobs = jobs.size();
            int activeJobs = totalJobs; // Assuming all jobs are active for now

            // Count total applications across all jobs
            int totalApplications = 0;
            for (Job job : jobs) {
                // You would need to implement application counting here
                // totalApplications += applicationRepository.countByJobId(job.getJobId());
            }

            return JobStatsDTO.builder()
                    .totalJobs(totalJobs)
                    .activeJobs(activeJobs)
                    .totalApplications(totalApplications)
                    .pendingReviews(totalApplications) // Assuming all are pending for now
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return JobStatsDTO.builder().build();
        }
    }

    private JobResponseDTO convertToResponseDTO(Job job) {
        return JobResponseDTO.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .salaryPerHour(job.getSalaryPerHour())
                .jobOverview(job.getJobOverview())
                .responsibilities(job.getResponsibilities())
                .requirements(job.getRequirements())
                .datePosted(job.getDatePosted())
                .updatedAt(job.getUpdatedAt())
                .employerProfileId(job.getEmployerProfile().getEmployerId())
                .companyName(job.getEmployerProfile().getCompanyName())
                .companyLogo(job.getEmployerProfile().getCompanyLogo())
                .websiteUrl(job.getEmployerProfile().getWebsiteUrl())
                .categoryId(job.getCategory().getCategoryId())
                .categoryName(job.getCategory().getName())
                .build();
    }
}