// CandidateProfileService.java
package lk.ijse.gdse.main.internbaybackend.service;

import lk.ijse.gdse.main.internbaybackend.dto.CandidateProfileDTO;

public interface CandidateProfileService {
    CandidateProfileDTO getCandidateProfile(Long userId);
    CandidateProfileDTO createOrUpdateCandidateProfile(Long userId, CandidateProfileDTO candidateProfileDTO);
    void deleteCandidateProfile(Long userId);
}