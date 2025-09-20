// ApplicationService.java
package lk.ijse.gdse.main.internbaybackend.service;

import lk.ijse.gdse.main.internbaybackend.dto.ApplicationCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ApplicationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
    ApplicationResponseDTO submitApplication(ApplicationCreateDTO applicationCreateDTO, Long candidateId);
    Page<ApplicationResponseDTO> getCandidateApplications(Long candidateId, Pageable pageable);
    Page<ApplicationResponseDTO> getJobApplications(Long jobId, Long employerId, Pageable pageable);
    Page<ApplicationResponseDTO> getEmployerApplications(Long employerId, String status, Pageable pageable);
    ApplicationResponseDTO getApplicationById(Long applicationId, Long userId, String userRole);
    ApplicationResponseDTO updateApplicationStatus(Long applicationId, String status, Long employerId);
    ApplicationResponseDTO withdrawApplication(Long applicationId, Long candidateId);
    void deleteApplication(Long applicationId, Long candidateId);
    Object getApplicationStatistics(Long employerId);
    boolean hasUserApplied(Long candidateId, Long jobId);
}