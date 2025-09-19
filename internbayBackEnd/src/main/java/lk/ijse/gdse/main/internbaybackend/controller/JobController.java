// JobController.java
package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.*;
import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lk.ijse.gdse.main.internbaybackend.service.JobService;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/jobs")
public class JobController {

    private final JobService jobService;
    private final JwtUtil jwtUtil;

    public JobController(JobService jobService, JwtUtil jwtUtil) {
        this.jobService = jobService;
        this.jwtUtil = jwtUtil;
    }

    // Extract email from JWT token
    private String extractEmailFromToken(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            return jwtUtil.getUsernameFromToken(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid token");
        }
    }

    // Post a new job
    @PostMapping
    public ResponseEntity<ResponsDto> createJob(@RequestHeader("Authorization") String token,
                                                @RequestBody JobCreateDTO jobCreateDTO) {
        try {
            String email = extractEmailFromToken(token);

            int result = jobService.createJob(email, jobCreateDTO);

            if (result == StatusList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponsDto(StatusList.Created, "Job created successfully", null));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Employer profile not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Failed to create job", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Get job by ID
    @GetMapping("/{jobId}")
    public ResponseEntity<ResponsDto> getJobById(@PathVariable Long jobId) {
        try {
            JobResponseDTO job = jobService.getJobById(jobId);

            if (job != null) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Job retrieved successfully", job));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Job not found", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Search jobs with filters and pagination - PUBLIC ACCESS
    @GetMapping("/search")
    public ResponseEntity<ResponsDto> searchJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datePosted") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String jobTypes,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary) {

        try {
            // Handle different sort parameter formats
            String actualSortBy = sortBy;
            String actualSortDir = sortDir;

            if (sortBy.contains(",")) {
                String[] sortParams = sortBy.split(",");
                actualSortBy = sortParams[0];
                actualSortDir = sortParams.length > 1 ? sortParams[1] : "desc";
            }

            Sort sort = actualSortDir.equalsIgnoreCase("desc") ?
                    Sort.by(actualSortBy).descending() :
                    Sort.by(actualSortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);

            // Parse job types
            List<JobType> jobTypeList = null;
            if (jobTypes != null && !jobTypes.isEmpty()) {
                try {
                    jobTypeList = List.of(jobTypes.split(","))
                            .stream()
                            .map(type -> JobType.valueOf(type.trim().toUpperCase()))
                            .toList();
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponsDto(StatusList.Bad_Request, "Invalid job type provided", null));
                }
            }

            Page<JobResponseDTO> jobs = jobService.searchJobs(
                    title, location, categoryId, jobTypeList, minSalary, maxSalary, pageable);

            return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Jobs retrieved successfully", jobs));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Get jobs by employer
    @GetMapping("/employer/{employerId}")
    public ResponseEntity<ResponsDto> getJobsByEmployer(
            @RequestHeader("Authorization") String token,
            @PathVariable Long employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datePosted,desc") String sort) {

        try {
            String email = extractEmailFromToken(token);

            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0];
            String sortDir = sortParams.length > 1 ? sortParams[1] : "desc";

            Sort sortObj = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sortObj);

            Page<JobResponseDTO> jobs = jobService.getJobsByEmployer(email, employerId, pageable);

            return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Jobs retrieved successfully", jobs));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Update job
    @PutMapping("/{jobId}")
    public ResponseEntity<ResponsDto> updateJob(@RequestHeader("Authorization") String token,
                                                @PathVariable Long jobId,
                                                @RequestBody JobCreateDTO jobUpdateDTO) {
        try {
            String email = extractEmailFromToken(token);

            int result = jobService.updateJob(email, jobId, jobUpdateDTO);

            if (result == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Job updated successfully", null));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Job not found", null));
            } else if (result == StatusList.Unauthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponsDto(StatusList.Unauthorized, "Not authorized to update this job", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Failed to update job", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Delete job
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ResponsDto> deleteJob(@RequestHeader("Authorization") String token,
                                                @PathVariable Long jobId) {
        try {
            String email = extractEmailFromToken(token);

            int result = jobService.deleteJob(email, jobId);

            if (result == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Job deleted successfully", null));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Job not found", null));
            } else if (result == StatusList.Unauthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponsDto(StatusList.Unauthorized, "Not authorized to delete this job", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Failed to delete job", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Get employer job statistics
    @GetMapping("/employer/{employerId}/stats")
    public ResponseEntity<ResponsDto> getEmployerJobStats(
            @RequestHeader("Authorization") String token,
            @PathVariable Long employerId) {

        try {
            String email = extractEmailFromToken(token);

            JobStatsDTO stats = jobService.getEmployerJobStats(email, employerId);

            return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Stats retrieved successfully", stats));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Toggle job status (active/inactive)
    @PatchMapping("/{jobId}/toggle-status")
    public ResponseEntity<ResponsDto> toggleJobStatus(@RequestHeader("Authorization") String token,
                                                      @PathVariable Long jobId) {
        try {
            String email = extractEmailFromToken(token);

            int result = jobService.toggleJobStatus(email, jobId);

            if (result == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Job status updated successfully", null));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Job not found", null));
            } else if (result == StatusList.Unauthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponsDto(StatusList.Unauthorized, "Not authorized to update this job", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Failed to update job status", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }
    @GetMapping("/all")
    public ResponseEntity<ResponsDto> getAllJobs() {
        try {
            List<JobResponseDTO> jobs = jobService.getAllJobs();
            return ResponseEntity.ok(new ResponsDto(StatusList.OK, "All jobs retrieved successfully", jobs));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }
}

