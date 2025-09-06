package lk.ijse.gdse.main.internbaybackend.dto;

import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Response DTO for returning job data
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponseDTO {
    private Long jobId;
    private String title;
    private String location;
    private JobType jobType;
    private String jobTypeDisplay;
    private BigDecimal salaryPerHour;
    private String jobOverview;
    private String responsibilities;
    private String requirements;
    private LocalDateTime datePosted;
    private LocalDateTime updatedAt;
    private String timeAgo;

    // Company/Employer information - Fixed: Should be String, not EmployerProfile
    private String companyName;
    private String companyEmail;
    private Long employerId;

    // Category information - Fixed: Should be String/Long, not Category
    private Long categoryId;
    private String categoryName;

    // Skills/Technologies (parsed from responsibilities/requirements)
    private List<String> skillsRequired;
}