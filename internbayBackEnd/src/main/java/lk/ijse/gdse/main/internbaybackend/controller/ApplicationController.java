/*
// ApplicationController.java (for handling job applications)
package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.ApplicationCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ApplicationResponseDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.service.ApplicationService;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtUtil jwtUtil;

    public ApplicationController(ApplicationService applicationService, JwtUtil jwtUtil) {
        this.applicationService = applicationService;
        this.jwtUtil = jwtUtil;
    }

    private String extractEmailFromToken(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            return jwtUtil.getUsernameFromToken(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid token");
        }
    }

    // Submit job application
    @PostMapping
    public ResponseEntity<ResponsDto> submitApplication(@RequestHeader("Authorization") String token,
                                                       @RequestBody ApplicationCreateDTO applicationDTO) {
        try {
            String email = extractEmailFromToken(token);
            
            int result = applicationService.submitApplication(email, applicationDTO);
            
            if (result == StatusList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponsDto(StatusList.Created, "Application submitted successfully", null));
            } else if (result == StatusList.Not_Acceptable) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponsDto(StatusList.Not_Acceptable, "You have already applied for this job", null));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Job not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Failed to submit application", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Get applications for a specific job (for employers)
    @GetMapping("/job/{jobId}")
    public ResponseEntity<ResponsDto> getJobApplications(@RequestHeader("Authorization") String token,
                                                        @PathVariable Long jobId) {
        try {
            String email = extractEmailFromToken(token);
            
            List<ApplicationResponseDTO> applications = applicationService.getJobApplications(email, jobId);
            
            return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Applications retrieved successfully", applications));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Update application status
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ResponsDto> updateApplicationStatus(@RequestHeader("Authorization") String token,
                                                             @PathVariable Long applicationId,
                                                             @RequestBody ApplicationStatusUpdateDTO statusUpdate) {
        try {
            String email = extractEmailFromToken(token);
            
            int result = applicationService.updateApplicationStatus(email, applicationId, statusUpdate.getStatus());
            
            if (result == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Application status updated successfully", null));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Application not found", null));
            } else if (result == StatusList.Unauthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponsDto(StatusList.Unauthorized, "Not authorized to update this application", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Failed to update application status", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }
}*/
