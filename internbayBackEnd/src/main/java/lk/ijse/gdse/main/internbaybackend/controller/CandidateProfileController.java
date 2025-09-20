// CandidateProfileController.java
package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.CandidateProfileDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.service.CandidateProfileService;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/candidate-profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;
    private final JwtUtil jwtUtil;

    /**
     * Get candidate profile
     */
    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> getCandidateProfile(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long userId = jwtUtil.getUserIdFromToken(token);

            CandidateProfileDTO profile = candidateProfileService.getCandidateProfile(userId);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Candidate profile retrieved successfully",
                    profile
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
     * Create or update candidate profile
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> createOrUpdateProfile(
            @Valid @RequestBody CandidateProfileDTO candidateProfileDTO,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long userId = jwtUtil.getUserIdFromToken(token);

            CandidateProfileDTO updatedProfile = candidateProfileService
                    .createOrUpdateCandidateProfile(userId, candidateProfileDTO);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Candidate profile updated successfully",
                    updatedProfile
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
     * Delete candidate profile
     */
    @DeleteMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResponsDto> deleteProfile(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long userId = jwtUtil.getUserIdFromToken(token);

            candidateProfileService.deleteCandidateProfile(userId);

            return ResponseEntity.ok(new ResponsDto(
                    HttpStatus.OK.value(),
                    "Candidate profile deleted successfully",
                    null
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
