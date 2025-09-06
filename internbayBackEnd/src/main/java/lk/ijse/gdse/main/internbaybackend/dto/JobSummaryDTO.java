package lk.ijse.gdse.main.internbaybackend.dto;

import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Summary DTO for job listings
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSummaryDTO {
    private Long jobId;
    private String title;
    private String companyName;  // Fixed: Should be String, not EmployerProfile
    private String location;
    private JobType jobType;
    private String jobTypeDisplay;
    private BigDecimal salaryPerHour;
    private String categoryName;  // Fixed: Should be String, not Category
    private LocalDateTime datePosted;
    private String timeAgo;
    private List<String> skills;
    private String shortDescription;
}