package lk.ijse.gdse.main.internbaybackend.dto;

import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobFilterDTO {
    private String title;
    private String location;
    private Long categoryId;  // Fixed: Should be Long, not Category
    private JobType jobType;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String sortBy; // newest, salary, relevance, company
    private String sortDirection; // asc, desc
    private Integer page;
    private Integer size;
}