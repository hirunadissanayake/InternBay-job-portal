
// EmployerProfileDTO.java (Your actual DTO)
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
public class EmployerProfileDTO {
    private Long employerId;
    private Long userId;
    private String userEmail; // For reference
    private String companyName;
    private Long industryId;
    private String industryName; // For display purposes
    private String websiteUrl;
    private String description;
    private String companyLogo; // URL for company logo
    private LocalDateTime updatedAt;

    // Additional fields for user data (not in DB but for frontend)
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePic;
}