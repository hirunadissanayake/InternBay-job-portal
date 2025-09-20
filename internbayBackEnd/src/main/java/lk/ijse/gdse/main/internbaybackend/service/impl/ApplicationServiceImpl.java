// Fixed ApplicationServiceImpl.java
package lk.ijse.gdse.main.internbaybackend.service.impl;

import lk.ijse.gdse.main.internbaybackend.dto.ApplicationCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ApplicationResponseDTO;
import lk.ijse.gdse.main.internbaybackend.dto.CandidateProfileDTO;
import lk.ijse.gdse.main.internbaybackend.entity.Application;
import lk.ijse.gdse.main.internbaybackend.entity.Job;
import lk.ijse.gdse.main.internbaybackend.entity.User;
import lk.ijse.gdse.main.internbaybackend.entity.ApplicationStatus;
import lk.ijse.gdse.main.internbaybackend.repository.ApplicationRepository;
import lk.ijse.gdse.main.internbaybackend.repository.JobRepository;
import lk.ijse.gdse.main.internbaybackend.repository.UserRepository;
import lk.ijse.gdse.main.internbaybackend.service.ApplicationService;
import lk.ijse.gdse.main.internbaybackend.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CandidateProfileService candidateProfileService;

    @Override
    public ApplicationResponseDTO submitApplication(ApplicationCreateDTO applicationCreateDTO, Long candidateId) {
        log.info("Submitting application for candidate: {} and job: {}", candidateId, applicationCreateDTO.getJobId());

        try {
            // Fixed: Handle both String and Long user IDs
            User candidate = userRepository.findById(candidateId.toString())
                    .orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + candidateId));

            // Check if job exists
            Job job = jobRepository.findById(applicationCreateDTO.getJobId())
                    .orElseThrow(() -> new RuntimeException("Job not found with ID: " + applicationCreateDTO.getJobId()));

            // Check if user already applied for this job
            if (applicationRepository.existsByUserUserIdAndJobJobId(candidateId, applicationCreateDTO.getJobId())) {
                throw new RuntimeException("You have already applied for this job");
            }

            // Check if job is still active
            if (!job.getIsActive()) {
                throw new RuntimeException("This job is no longer accepting applications");
            }

            // Update/Create candidate profile with the submitted data
            CandidateProfileDTO profileDTO = CandidateProfileDTO.builder()
                    .educationBackground(applicationCreateDTO.getEducationBackground())
                    .workExperience(applicationCreateDTO.getWorkExperience())
                    .skills(applicationCreateDTO.getSkills())
                    .resumeUrl(applicationCreateDTO.getResumeUrl())
                    .build();

            candidateProfileService.createOrUpdateCandidateProfile(candidateId, profileDTO);

            // Create new application
            Application application = Application.builder()
                    .user(candidate)
                    .job(job)
                    .resumeUrl(applicationCreateDTO.getResumeUrl())
                    .status(ApplicationStatus.SUBMITTED)
                    .appliedAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Application savedApplication = applicationRepository.save(application);
            log.info("Application saved successfully with ID: {}", savedApplication.getApplicationId());

            return convertToResponseDTO(savedApplication);

        } catch (Exception e) {
            log.error("Error submitting application: ", e);
            throw new RuntimeException("Failed to submit application: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> getCandidateApplications(Long candidateId, Pageable pageable) {
        log.info("Getting applications for candidate: {}", candidateId);

        try {
            Page<Application> applications = applicationRepository.findByUserUserIdOrderByAppliedAtDesc(candidateId, pageable);
            log.info("Found {} applications for candidate {}", applications.getTotalElements(), candidateId);

            return applications.map(this::convertToResponseDTO);
        } catch (Exception e) {
            log.error("Error getting candidate applications: ", e);
            throw new RuntimeException("Failed to get applications: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> getJobApplications(Long jobId, Long employerId, Pageable pageable) {
        try {
            // Verify that the employer owns this job
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            // Check employer ownership - handle potential null values safely
            if (job.getEmployerProfile() == null ||
                    job.getEmployerProfile().getUser() == null ||
                    !job.getEmployerProfile().getUser().getUserId().equals(employerId)) {
                throw new RuntimeException("You don't have permission to view applications for this job");
            }

            Page<Application> applications = applicationRepository.findByJobJobIdOrderByAppliedAtDesc(jobId, pageable);
            return applications.map(this::convertToResponseDTO);
        } catch (Exception e) {
            log.error("Error getting job applications: ", e);
            throw new RuntimeException("Failed to get job applications: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> getEmployerApplications(Long employerId, String status, Pageable pageable) {
        try {
            Page<Application> applications;

            if (status != null && !status.isEmpty()) {
                ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
                applications = applicationRepository.findByJobEmployerProfileUserUserIdAndStatusOrderByAppliedAtDesc(employerId, appStatus, pageable);
            } else {
                applications = applicationRepository.findByJobEmployerProfileUserUserIdOrderByAppliedAtDesc(employerId, pageable);
            }

            return applications.map(this::convertToResponseDTO);
        } catch (Exception e) {
            log.error("Error getting employer applications: ", e);
            throw new RuntimeException("Failed to get employer applications: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponseDTO getApplicationById(Long applicationId, Long userId, String userRole) {
        try {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Check permissions based on role
            if ("CANDIDATE".equals(userRole)) {
                if (!application.getUser().getUserId().equals(userId)) {
                    throw new RuntimeException("You don't have permission to view this application");
                }
            } else if ("EMPLOYEE".equals(userRole)) {
                if (application.getJob().getEmployerProfile() == null ||
                        application.getJob().getEmployerProfile().getUser() == null ||
                        !application.getJob().getEmployerProfile().getUser().getUserId().equals(userId)) {
                    throw new RuntimeException("You don't have permission to view this application");
                }
            } else {
                throw new RuntimeException("Invalid user role");
            }

            return convertToResponseDTO(application);
        } catch (Exception e) {
            log.error("Error getting application by ID: ", e);
            throw new RuntimeException("Failed to get application: " + e.getMessage(), e);
        }
    }

    @Override
    public ApplicationResponseDTO updateApplicationStatus(Long applicationId, String status, Long employerId) {
        try {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify employer owns the job
            if (application.getJob().getEmployerProfile() == null ||
                    application.getJob().getEmployerProfile().getUser() == null ||
                    !application.getJob().getEmployerProfile().getUser().getUserId().equals(employerId)) {
                throw new RuntimeException("You don't have permission to update this application");
            }

            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            application.setStatus(newStatus);
            application.setUpdatedAt(LocalDateTime.now());

            Application savedApplication = applicationRepository.save(application);
            return convertToResponseDTO(savedApplication);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid application status: " + status);
        } catch (Exception e) {
            log.error("Error updating application status: ", e);
            throw new RuntimeException("Failed to update application status: " + e.getMessage(), e);
        }
    }

    @Override
    public ApplicationResponseDTO withdrawApplication(Long applicationId, Long candidateId) {
        try {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify candidate owns the application
            if (!application.getUser().getUserId().equals(candidateId)) {
                throw new RuntimeException("You don't have permission to withdraw this application");
            }

            // Check if application can be withdrawn
            if (application.getStatus() == ApplicationStatus.ACCEPTED ||
                    application.getStatus() == ApplicationStatus.REJECTED) {
                throw new RuntimeException("Cannot withdraw application that has already been processed");
            }

            application.setStatus(ApplicationStatus.WITHDRAWN);
            application.setUpdatedAt(LocalDateTime.now());
            Application savedApplication = applicationRepository.save(application);
            return convertToResponseDTO(savedApplication);
        } catch (Exception e) {
            log.error("Error withdrawing application: ", e);
            throw new RuntimeException("Failed to withdraw application: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteApplication(Long applicationId, Long candidateId) {
        try {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify candidate owns the application
            if (!application.getUser().getUserId().equals(candidateId)) {
                throw new RuntimeException("You don't have permission to delete this application");
            }

            // Check if application can be deleted (within 24 hours)
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            if (application.getAppliedAt().isBefore(twentyFourHoursAgo)) {
                throw new RuntimeException("Applications can only be deleted within 24 hours of submission");
            }

            // Only allow deletion if status is SUBMITTED
            if (application.getStatus() != ApplicationStatus.SUBMITTED) {
                throw new RuntimeException("Can only delete submitted applications");
            }

            applicationRepository.delete(application);
        } catch (Exception e) {
            log.error("Error deleting application: ", e);
            throw new RuntimeException("Failed to delete application: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getApplicationStatistics(Long employerId) {
        try {
            List<Application> applications = applicationRepository.findByJobEmployerProfileUserUserId(employerId);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", applications.size());
            statistics.put("submitted", applications.stream().mapToInt(a -> a.getStatus() == ApplicationStatus.SUBMITTED ? 1 : 0).sum());
            statistics.put("reviewed", applications.stream().mapToInt(a -> a.getStatus() == ApplicationStatus.REVIEWED ? 1 : 0).sum());
            statistics.put("shortlisted", applications.stream().mapToInt(a -> a.getStatus() == ApplicationStatus.SHORTLISTED ? 1 : 0).sum());
            statistics.put("accepted", applications.stream().mapToInt(a -> a.getStatus() == ApplicationStatus.ACCEPTED ? 1 : 0).sum());
            statistics.put("rejected", applications.stream().mapToInt(a -> a.getStatus() == ApplicationStatus.REJECTED ? 1 : 0).sum());
            statistics.put("withdrawn", applications.stream().mapToInt(a -> a.getStatus() == ApplicationStatus.WITHDRAWN ? 1 : 0).sum());

            return statistics;
        } catch (Exception e) {
            log.error("Error getting application statistics: ", e);
            throw new RuntimeException("Failed to get application statistics: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserApplied(Long candidateId, Long jobId) {
        return applicationRepository.existsByUserUserIdAndJobJobId(candidateId, jobId);
    }

    private ApplicationResponseDTO convertToResponseDTO(Application application) {
        try {
            Long candidateId;
            try {
                // Handle both String and Long user IDs
                candidateId = Long.valueOf(application.getUser().getUserId());
            } catch (NumberFormatException e) {
                log.error("Error converting user ID to Long: {}", application.getUser().getUserId());
                candidateId = 0L; // Default value
            }

            return ApplicationResponseDTO.builder()
                    .applicationId(application.getApplicationId())
                    .jobId(application.getJob().getJobId())
                    .jobTitle(application.getJob().getTitle())
                    .companyName(getCompanyName(application))
                    .jobLocation(application.getJob().getLocation())
                    .jobType(application.getJob().getJobType() != null ? application.getJob().getJobType().name() : "NOT_SPECIFIED")
                    .salary(application.getJob().getSalaryPerHour())
                    .candidateId(candidateId)
                    .candidateName(getCandidateName(application))
                    .candidateEmail(application.getUser().getEmail())
                    .resumeUrl(application.getResumeUrl())
                    .status(application.getStatus().name())
                    .appliedDate(application.getAppliedAt())
                    .updatedAt(application.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error converting application to DTO: ", e);
            log.error("Application details: jobId={}, userId={}, status={}",
                    application.getJob().getJobId(),
                    application.getUser().getUserId(),
                    application.getStatus());
            throw new RuntimeException("Error converting application to DTO: " + e.getMessage(), e);
        }
    }

    private String getCompanyName(Application application) {
        try {
            if (application.getJob() != null &&
                    application.getJob().getEmployerProfile() != null &&
                    application.getJob().getEmployerProfile().getCompanyName() != null) {
                return application.getJob().getEmployerProfile().getCompanyName();
            }

            // Fallback: try to get company name from user if available
            if (application.getJob() != null &&
                    application.getJob().getEmployerProfile() != null &&
                    application.getJob().getEmployerProfile().getUser() != null) {
                User employer = application.getJob().getEmployerProfile().getUser();
                String companyName = (employer.getFirstName() != null ? employer.getFirstName() + " " : "") +
                        (employer.getLastName() != null ? employer.getLastName() : "");
                if (!companyName.trim().isEmpty()) {
                    return companyName.trim();
                }
            }

            return "Company Name Not Available";
        } catch (Exception e) {
            log.warn("Error getting company name for application {}: ", application.getApplicationId(), e);
            return "Company Name Not Available";
        }
    }

    // Enhanced getCandidateName method
    private String getCandidateName(Application application) {
        try {
            User user = application.getUser();
            if (user == null) {
                return "Name Not Available";
            }

            String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
            String lastName = user.getLastName() != null ? user.getLastName().trim() : "";

            String fullName = (firstName + " " + lastName).trim();

            if (fullName.isEmpty()) {
                // Fallback to email prefix
                String email = user.getEmail();
                if (email != null && email.contains("@")) {
                    return email.substring(0, email.indexOf("@"));
                }
                return "Name Not Available";
            }

            return fullName;
        } catch (Exception e) {
            log.warn("Error getting candidate name for application {}: ", application.getApplicationId(), e);
            return "Name Not Available";
        }
    }
}