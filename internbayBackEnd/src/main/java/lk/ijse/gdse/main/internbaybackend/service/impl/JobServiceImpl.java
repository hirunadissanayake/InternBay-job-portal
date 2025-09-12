package lk.ijse.gdse.main.internbaybackend.service.impl;

import lk.ijse.gdse.main.internbaybackend.dto.JobCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.JobResponseDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.entity.*;
import lk.ijse.gdse.main.internbaybackend.repository.CategoryRepository;
import lk.ijse.gdse.main.internbaybackend.repository.EmployerProfileRepository;
import lk.ijse.gdse.main.internbaybackend.repository.JobRepository;
import lk.ijse.gdse.main.internbaybackend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ResponsDto createJob(JobCreateDTO jobCreateDTO) {
        try {
            // Validate employer profile exists
            Optional<EmployerProfile> employerProfileOpt = employerProfileRepository
                    .findById(jobCreateDTO.getEmployerProfileId());
            if (employerProfileOpt.isEmpty()) {
                return new ResponsDto(404, "Employer profile not found", null);
            }

            // Validate category exists
            Optional<Category> categoryOpt = categoryRepository
                    .findById(jobCreateDTO.getCategoryId());
            if (categoryOpt.isEmpty()) {
                return new ResponsDto(404, "Category not found", null);
            }

            // Create job entity
            Job job = Job.builder()
                    .title(jobCreateDTO.getTitle())
                    .location(jobCreateDTO.getLocation())
                    .jobType(jobCreateDTO.getJobType())
                    .salaryPerHour(jobCreateDTO.getSalaryPerHour())
                    .jobOverview(jobCreateDTO.getJobOverview())
                    .responsibilities(jobCreateDTO.getResponsibilities())
                    .requirements(jobCreateDTO.getRequirements())
                    .employerProfile(employerProfileOpt.get())
                    .category(categoryOpt.get())
                    .datePosted(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save job
            Job savedJob = jobRepository.save(job);

            // Convert to response DTO
            JobResponseDTO responseDTO = convertToResponseDTO(savedJob);

            return new ResponsDto(201, "Job created successfully", responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponsDto(500, "Error creating job: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponsDto getJobById(Long jobId) {
        try {
            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return new ResponsDto(404, "Job not found", null);
            }

            JobResponseDTO responseDTO = convertToResponseDTO(jobOpt.get());
            return new ResponsDto(200, "Job found", responseDTO);

        } catch (Exception e) {
            return new ResponsDto(500, "Error fetching job: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponsDto getAllJobs(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("datePosted").descending());
            Page<Job> jobPage = jobRepository.findAll(pageable);

            List<JobResponseDTO> jobDTOs = jobPage.getContent().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return new ResponsDto(200, "Jobs fetched successfully", jobDTOs);

        } catch (Exception e) {
            return new ResponsDto(500, "Error fetching jobs: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponsDto getJobsByEmployer(Long employerId) {
        try {
            List<Job> jobs = jobRepository.findByEmployerProfileEmployerId(employerId);
            List<JobResponseDTO> jobDTOs = jobs.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return new ResponsDto(200, "Employer jobs fetched successfully", jobDTOs);

        } catch (Exception e) {
            return new ResponsDto(500, "Error fetching employer jobs: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponsDto searchJobs(String title, Long categoryId, JobType jobType,
                                BigDecimal minSalary, BigDecimal maxSalary,
                                String location, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("datePosted").descending());
            Page<Job> jobPage = jobRepository.findJobsWithFilters(
                    title, categoryId, jobType, minSalary, maxSalary, location, pageable);

            List<JobResponseDTO> jobDTOs = jobPage.getContent().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return new ResponsDto(200, "Jobs searched successfully", jobDTOs);

        } catch (Exception e) {
            return new ResponsDto(500, "Error searching jobs: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponsDto updateJob(Long jobId, JobCreateDTO jobCreateDTO) {
        try {
            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return new ResponsDto(404, "Job not found", null);
            }

            Job job = jobOpt.get();

            // Validate and update category if provided
            if (jobCreateDTO.getCategoryId() != null) {
                Optional<Category> categoryOpt = categoryRepository
                        .findById(jobCreateDTO.getCategoryId());
                if (categoryOpt.isEmpty()) {
                    return new ResponsDto(404, "Category not found", null);
                }
                job.setCategory(categoryOpt.get());
            }

            // Update job fields
            if (jobCreateDTO.getTitle() != null) job.setTitle(jobCreateDTO.getTitle());
            if (jobCreateDTO.getLocation() != null) job.setLocation(jobCreateDTO.getLocation());
            if (jobCreateDTO.getJobType() != null) job.setJobType(jobCreateDTO.getJobType());
            if (jobCreateDTO.getSalaryPerHour() != null) job.setSalaryPerHour(jobCreateDTO.getSalaryPerHour());
            if (jobCreateDTO.getJobOverview() != null) job.setJobOverview(jobCreateDTO.getJobOverview());
            if (jobCreateDTO.getResponsibilities() != null) job.setResponsibilities(jobCreateDTO.getResponsibilities());
            if (jobCreateDTO.getRequirements() != null) job.setRequirements(jobCreateDTO.getRequirements());

            job.setUpdatedAt(LocalDateTime.now());

            Job updatedJob = jobRepository.save(job);
            JobResponseDTO responseDTO = convertToResponseDTO(updatedJob);

            return new ResponsDto(200, "Job updated successfully", responseDTO);

        } catch (Exception e) {
            return new ResponsDto(500, "Error updating job: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponsDto deleteJob(Long jobId) {
        try {
            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return new ResponsDto(404, "Job not found", null);
            }

            jobRepository.deleteById(jobId);
            return new ResponsDto(200, "Job deleted successfully", null);

        } catch (Exception e) {
            return new ResponsDto(500, "Error deleting job: " + e.getMessage(), null);
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