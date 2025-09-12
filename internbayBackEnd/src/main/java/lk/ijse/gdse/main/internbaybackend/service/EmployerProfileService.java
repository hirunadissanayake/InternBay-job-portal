package lk.ijse.gdse.main.internbaybackend.service;

import lk.ijse.gdse.main.internbaybackend.dto.EmployerProfileDTO;

public interface EmployerProfileService {

    /**
     * Get employer profile by user email
     */
    EmployerProfileDTO getEmployerProfileByEmail(String email);

    /**
     * Create or update employer profile
     */
    int createOrUpdateEmployerProfile(String email, EmployerProfileDTO employerProfileDTO);

    /**
     * Update company logo
     */
    int updateCompanyLogo(String email, String companyLogoUrl);

    /**
     * Delete employer profile
     */
    int deleteEmployerProfile(String email);

    /**
     * Check if employer profile exists for user
     */
    boolean employerProfileExists(String email);
}