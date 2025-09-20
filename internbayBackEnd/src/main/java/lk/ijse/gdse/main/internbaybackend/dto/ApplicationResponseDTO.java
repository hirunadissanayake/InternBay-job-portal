// ApplicationResponseDTO.java
package lk.ijse.gdse.main.internbaybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDTO {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;        // Add this
    private String companyName;     // Add this
    private String jobLocation;     // Add this
    private String jobType;         // Add this
    private BigDecimal salary;      // Add this
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String resumeUrl;
    private String status;
    private LocalDateTime appliedDate;
    private LocalDateTime updatedAt;
}