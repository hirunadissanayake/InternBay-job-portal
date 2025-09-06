package lk.ijse.gdse.main.internbaybackend.dto;

import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// Request DTO for creating/updating jobs
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequestDTO {
    private String title;
    private String location;
    private JobType jobType;
    private BigDecimal salaryPerHour;
    private String jobOverview;
    private List<String> responsibilities;
    private List<String> requirements;
    private Long categoryId;  // Fixed: Should be Long, not Category
}