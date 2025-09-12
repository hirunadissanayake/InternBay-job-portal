package lk.ijse.gdse.main.internbaybackend.dto;

import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCreateDTO {
    private String title;
    private String location;
    private JobType jobType;
    private BigDecimal salaryPerHour;
    private String jobOverview;
    private List<String> responsibilities;
    private List<String> requirements;
    private Long categoryId;
    private Long employerProfileId;
}