// 2. JobResponseDTO.java
package lk.ijse.gdse.main.internbaybackend.dto;

import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponseDTO {
    private Long jobId;
    private String title;
    private String location;
    private JobType jobType;
    private BigDecimal salaryPerHour;
    private String jobOverview;
    private List<String> responsibilities;
    private List<String> requirements;
    private LocalDateTime datePosted;
    private LocalDateTime updatedAt;
    
    // Employer Profile Info
    private Long employerProfileId;
    private String companyName;
    private String companyLogo;
    private String websiteUrl;
    
    // Category Info
    private Long categoryId;
    private String categoryName;
}