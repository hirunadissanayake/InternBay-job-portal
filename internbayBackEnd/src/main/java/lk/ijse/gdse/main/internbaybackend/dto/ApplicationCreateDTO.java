// ApplicationCreateDTO.java
package lk.ijse.gdse.main.internbaybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreateDTO {
    private Long jobId;
    private String resumeUrl;

    // Profile data that will be saved to CandidateProfile
    private String educationBackground;
    private String workExperience;
    private String skills;
}