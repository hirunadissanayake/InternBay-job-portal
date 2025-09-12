package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.*;
import lk.ijse.gdse.main.internbaybackend.service.EmployerProfileService;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/employer")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;
    private final JwtUtil jwtUtil;

    public EmployerProfileController(EmployerProfileService employerProfileService, JwtUtil jwtUtil) {
        this.employerProfileService = employerProfileService;
        this.jwtUtil = jwtUtil;
    }

    // Helper method to extract email from JWT token
    private String extractEmailFromToken(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            return jwtUtil.getUsernameFromToken(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid token");
        }
    }

    // Get employer profile
    @GetMapping("/profile")
    public ResponseEntity<ResponsDto> getEmployerProfile(@RequestHeader("Authorization") String token) {
        try {
            String email = extractEmailFromToken(token);

            EmployerProfileDTO employerProfile = employerProfileService.getEmployerProfileByEmail(email);

            if (employerProfile != null) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Employer profile retrieved successfully", employerProfile));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Employer profile not found", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Create or update employer profile
    @PostMapping("/profile")
    public ResponseEntity<ResponsDto> createOrUpdateEmployerProfile(@RequestHeader("Authorization") String token,
                                                                    @RequestBody EmployerProfileDTO employerProfileDTO) {
        try {
            String email = extractEmailFromToken(token);

            // Log the received data for debugging
            System.out.println("Received employer profile data: " + employerProfileDTO);

            int result = employerProfileService.createOrUpdateEmployerProfile(email, employerProfileDTO);

            switch (result) {
                case StatusList.Created:
                    EmployerProfileDTO createdProfile = employerProfileService.getEmployerProfileByEmail(email);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponsDto(StatusList.Created, "Employer profile created successfully", createdProfile));

                case StatusList.OK:
                    EmployerProfileDTO updatedProfile = employerProfileService.getEmployerProfileByEmail(email);
                    return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Employer profile updated successfully", updatedProfile));

                case StatusList.Not_Found:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponsDto(StatusList.Not_Found, "User not found", null));

                case StatusList.Bad_Request:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponsDto(StatusList.Bad_Request, "Invalid industry category", null));

                default:
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponsDto(StatusList.Bad_Gateway, "Operation failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in createOrUpdateEmployerProfile endpoint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, "Failed to update profile: " + e.getMessage(), null));
        }
    }

    // Update company logo - FIXED with better error handling
    @PutMapping("/profile/logo")
    public ResponseEntity<ResponsDto> updateCompanyLogo(@RequestHeader("Authorization") String token,
                                                        @RequestBody CompanyLogoDTO companyLogoDTO) {
        try {
            String email = extractEmailFromToken(token);

            // Validate input
            if (companyLogoDTO == null || companyLogoDTO.getCompanyLogoUrl() == null ||
                    companyLogoDTO.getCompanyLogoUrl().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Company logo URL is required", null));
            }

            System.out.println("Updating company logo for user: " + email + " with URL: " + companyLogoDTO.getCompanyLogoUrl());

            int result = employerProfileService.updateCompanyLogo(email, companyLogoDTO.getCompanyLogoUrl());

            if (result == StatusList.OK) {
                // Return the updated profile to confirm the logo was saved
                EmployerProfileDTO updatedProfile = employerProfileService.getEmployerProfileByEmail(email);
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Company logo updated successfully", updatedProfile));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Employer profile not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Logo update failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in updateCompanyLogo endpoint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, "Failed to update logo: " + e.getMessage(), null));
        }
    }

    // Delete employer profile
    @DeleteMapping("/profile")
    public ResponseEntity<ResponsDto> deleteEmployerProfile(@RequestHeader("Authorization") String token) {
        try {
            String email = extractEmailFromToken(token);

            int result = employerProfileService.deleteEmployerProfile(email);

            if (result == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Employer profile deleted successfully", null));
            } else if (result == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "Employer profile not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Profile deletion failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }
}