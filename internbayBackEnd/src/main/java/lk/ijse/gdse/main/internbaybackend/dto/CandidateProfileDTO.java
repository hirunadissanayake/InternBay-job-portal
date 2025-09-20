package lk.ijse.gdse.main.internbaybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileDTO {
    private Long profileId;
    private Long userId;
    private String educationBackground;
    private String workExperience;
    private String skills;
    private String resumeUrl;
    private LocalDateTime updatedAt;
}