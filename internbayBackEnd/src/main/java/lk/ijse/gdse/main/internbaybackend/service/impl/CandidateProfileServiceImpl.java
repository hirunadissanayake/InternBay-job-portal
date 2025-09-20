package lk.ijse.gdse.main.internbaybackend.service.impl;

import lk.ijse.gdse.main.internbaybackend.dto.CandidateProfileDTO;
import lk.ijse.gdse.main.internbaybackend.entity.CandidateProfile;
import lk.ijse.gdse.main.internbaybackend.entity.User;
import lk.ijse.gdse.main.internbaybackend.repository.CandidateProfileRepository;
import lk.ijse.gdse.main.internbaybackend.repository.UserRepository;
import lk.ijse.gdse.main.internbaybackend.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CandidateProfileDTO getCandidateProfile(Long userId) {
        CandidateProfile profile = candidateProfileRepository.findByUserUserId(userId)
                .orElse(null);
        
        if (profile == null) {
            // Return empty profile if not exists
            return CandidateProfileDTO.builder()
                    .userId(userId)
                    .educationBackground("")
                    .workExperience("")
                    .skills("")
                    .resumeUrl("")
                    .build();
        }
        
        return convertToDTO(profile);
    }

    @Override
    public CandidateProfileDTO createOrUpdateCandidateProfile(Long userId, CandidateProfileDTO candidateProfileDTO) {
        User user = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        CandidateProfile profile = candidateProfileRepository.findByUserUserId(userId)
                .orElse(CandidateProfile.builder()
                        .user(user)
                        .build());

        // Update profile fields
        profile.setEducationBackground(candidateProfileDTO.getEducationBackground());
        profile.setWorkExperience(candidateProfileDTO.getWorkExperience());
        profile.setSkills(candidateProfileDTO.getSkills());
        profile.setResumeUrl(candidateProfileDTO.getResumeUrl());
        profile.setUpdatedAt(LocalDateTime.now());

        CandidateProfile savedProfile = candidateProfileRepository.save(profile);
        return convertToDTO(savedProfile);
    }

    @Override
    public void deleteCandidateProfile(Long userId) {
        CandidateProfile profile = candidateProfileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));
        
        candidateProfileRepository.delete(profile);
    }

    private CandidateProfileDTO convertToDTO(CandidateProfile profile) {
        return CandidateProfileDTO.builder()
                .profileId(profile.getProfileId())
                .userId(Long.valueOf(profile.getUser().getUserId()))
                .educationBackground(profile.getEducationBackground())
                .workExperience(profile.getWorkExperience())
                .skills(profile.getSkills())
                .resumeUrl(profile.getResumeUrl())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}