// ApplicationResponseDTO.java
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
public class ApplicationResponseDTO {
    private Long applicationId;
    private Long jobId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String resumeUrl;
    private String status;
    private LocalDateTime appliedDate;
}