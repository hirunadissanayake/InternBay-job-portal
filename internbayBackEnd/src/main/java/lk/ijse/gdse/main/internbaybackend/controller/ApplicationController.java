package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.ApplicationCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ApplicationResponseDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ApplicationStatusUpdateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.service.ApplicationService;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtUtil jwtUtil;

    /**
     * Submit a new job application (Candidates only)
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> submitApplication(
            @Valid @RequestBody ApplicationCreateDTO applicationCreateDTO,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long candidateId = jwtUtil.getUserIdFromToken(token);

            ApplicationResponseDTO response = applicationService.submitApplication(
                    applicationCreateDTO, candidateId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponsDto(
                            HttpStatus.CREATED.value(),
                            "Application submitted successfully",
                            response
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsDto(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get all applications for a candidate (Candidates only)
     */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> getCandidateApplications(
            HttpServletRequest request,
            Pageable pageable) {
        try {
            String token = extractTokenFromRequest(request);
            Long candidateId = jwtUtil.getUserIdFromToken(token);

            Page<ApplicationResponseDTO> applications = applicationService
                    .getCandidateApplications(candidateId, pageable);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Candidate applications retrieved successfully",
                    applications
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get all applications for a specific job (Employers only)
     */
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ResponsDto> getJobApplications(
            @PathVariable Long jobId,
            HttpServletRequest request,
            Pageable pageable) {
        try {
            String token = extractTokenFromRequest(request);
            Long employerId = jwtUtil.getUserIdFromToken(token);

            Page<ApplicationResponseDTO> applications = applicationService
                    .getJobApplications(jobId, employerId, pageable);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Job applications retrieved successfully",
                    applications
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsDto(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get all applications for an employer's jobs
     */
    @GetMapping("/employer/all")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ResponsDto> getEmployerApplications(
            HttpServletRequest request,
            Pageable pageable,
            @RequestParam(required = false) String status) {
        try {
            String token = extractTokenFromRequest(request);
            Long employerId = jwtUtil.getUserIdFromToken(token);

            Page<ApplicationResponseDTO> applications = applicationService
                    .getEmployerApplications(employerId, status, pageable);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Employer applications retrieved successfully",
                    applications
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get application by ID
     */
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponsDto> getApplicationById(
            @PathVariable Long applicationId,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String userRole = jwtUtil.getRoleFromToken(token);

            ApplicationResponseDTO application = applicationService
                    .getApplicationById(applicationId, userId, userRole);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Application retrieved successfully",
                    application
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsDto(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Update application status (Employers only)
     */
    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ResponsDto> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusUpdateDTO statusUpdateDTO,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long employerId = jwtUtil.getUserIdFromToken(token);

            ApplicationResponseDTO updatedApplication = applicationService
                    .updateApplicationStatus(applicationId, statusUpdateDTO.getStatus(), employerId);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Application status updated successfully",
                    updatedApplication
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsDto(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Withdraw application (Candidates only)
     */
    @PutMapping("/{applicationId}/withdraw")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> withdrawApplication(
            @PathVariable Long applicationId,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long candidateId = jwtUtil.getUserIdFromToken(token);

            ApplicationResponseDTO withdrawnApplication = applicationService
                    .withdrawApplication(applicationId, candidateId);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Application withdrawn successfully",
                    withdrawnApplication
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsDto(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Delete application (Candidates only - within 24 hours of submission)
     */
    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> deleteApplication(
            @PathVariable Long applicationId,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long candidateId = jwtUtil.getUserIdFromToken(token);

            applicationService.deleteApplication(applicationId, candidateId);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Application deleted successfully",
                    "Application has been permanently deleted"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsDto(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get application statistics for employer
     */
    @GetMapping("/employer/statistics")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ResponsDto> getApplicationStatistics(
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long employerId = jwtUtil.getUserIdFromToken(token);

            Object statistics = applicationService.getApplicationStatistics(employerId);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Application statistics retrieved successfully",
                    statistics
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Check if candidate has already applied for a job
     */
    @GetMapping("/check/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> checkApplicationExists(
            @PathVariable Long jobId,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long candidateId = jwtUtil.getUserIdFromToken(token);

            boolean exists = applicationService.hasUserApplied(candidateId, jobId);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    exists ? "Application exists" : "No application found",
                    exists
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Extract JWT token from request header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("JWT Token is missing");
    }
}