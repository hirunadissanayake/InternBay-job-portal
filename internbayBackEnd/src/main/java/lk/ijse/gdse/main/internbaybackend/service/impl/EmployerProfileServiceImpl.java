package lk.ijse.gdse.main.internbaybackend.service.impl;

import lk.ijse.gdse.main.internbaybackend.dto.EmployerProfileDTO;
import lk.ijse.gdse.main.internbaybackend.entity.Category;
import lk.ijse.gdse.main.internbaybackend.entity.EmployerProfile;
import lk.ijse.gdse.main.internbaybackend.entity.User;
import lk.ijse.gdse.main.internbaybackend.repository.CategoryRepository;
import lk.ijse.gdse.main.internbaybackend.repository.EmployerProfileRepository;
import lk.ijse.gdse.main.internbaybackend.repository.UserRepository;
import lk.ijse.gdse.main.internbaybackend.service.EmployerProfileService;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class EmployerProfileServiceImpl implements EmployerProfileService {

    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public EmployerProfileServiceImpl(EmployerProfileRepository employerProfileRepository,
                                      UserRepository userRepository,
                                      CategoryRepository categoryRepository,
                                      ModelMapper modelMapper) {
        this.employerProfileRepository = employerProfileRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public EmployerProfileDTO getEmployerProfileByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return null;
            }

            Optional<EmployerProfile> employerProfileOpt = employerProfileRepository.findByUser(user);

            if (employerProfileOpt.isPresent()) {
                EmployerProfile employerProfile = employerProfileOpt.get();

                // Create DTO and populate fields
                EmployerProfileDTO dto = EmployerProfileDTO.builder()
                        .employerId(employerProfile.getEmployerId())
                        .userId(user.getUserId().longValue())
                        .userEmail(user.getEmail())
                        .companyName(employerProfile.getCompanyName())
                        .websiteUrl(employerProfile.getWebsiteUrl())
                        .description(employerProfile.getDescription())
                        .companyLogo(employerProfile.getCompanyLogo())
                        .updatedAt(employerProfile.getUpdatedAt())
                        // Add user details for frontend
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .phone(user.getPhone())
                        .profilePic(user.getProfilePic())
                        .build();

                // Set industry information
                if (employerProfile.getIndustry() != null) {
                    dto.setIndustryId(employerProfile.getIndustry().getCategoryId());
                    dto.setIndustryName(employerProfile.getIndustry().getName());
                }

                return dto;
            } else {
                // Return user info even if no employer profile exists
                return EmployerProfileDTO.builder()
                        .userId(user.getUserId().longValue())
                        .userEmail(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .phone(user.getPhone())
                        .profilePic(user.getProfilePic())
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int createOrUpdateEmployerProfile(String email, EmployerProfileDTO employerProfileDTO) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return StatusList.Not_Found;
            }

            // Find industry category if industryName is provided
            Category industry = null;
            if (employerProfileDTO.getIndustryName() != null && !employerProfileDTO.getIndustryName().trim().isEmpty()) {
                Optional<Category> categoryOpt = Optional.ofNullable(categoryRepository.findByName(employerProfileDTO.getIndustryName()));
                if (categoryOpt.isEmpty()) {
                    return StatusList.Bad_Request; // Invalid industry
                }
                industry = categoryOpt.get();
            } else if (employerProfileDTO.getIndustryId() != null) {
                // Try to find by ID if provided
                Optional<Category> categoryOpt = categoryRepository.findById(employerProfileDTO.getIndustryId());
                if (categoryOpt.isEmpty()) {
                    return StatusList.Bad_Request; // Invalid industry ID
                }
                industry = categoryOpt.get();
            }

            Optional<EmployerProfile> existingProfileOpt = employerProfileRepository.findByUser(user);

            if (existingProfileOpt.isPresent()) {
                // Update existing profile
                EmployerProfile existingProfile = existingProfileOpt.get();
                existingProfile.setCompanyName(employerProfileDTO.getCompanyName());
                existingProfile.setIndustry(industry);
                existingProfile.setWebsiteUrl(employerProfileDTO.getWebsiteUrl());
                existingProfile.setDescription(employerProfileDTO.getDescription());
                existingProfile.setUpdatedAt(LocalDateTime.now());

                // Update company logo if provided (FIXED: Handle both cases)
                if (employerProfileDTO.getCompanyLogo() != null && !employerProfileDTO.getCompanyLogo().trim().isEmpty()) {
                    existingProfile.setCompanyLogo(employerProfileDTO.getCompanyLogo());
                }

                employerProfileRepository.save(existingProfile);
                return StatusList.OK;
            } else {
                // Create new profile (FIXED: Include logo in creation)
                EmployerProfile newProfile = EmployerProfile.builder()
                        .user(user)
                        .companyName(employerProfileDTO.getCompanyName())
                        .industry(industry)
                        .websiteUrl(employerProfileDTO.getWebsiteUrl())
                        .description(employerProfileDTO.getDescription())
                        .companyLogo(employerProfileDTO.getCompanyLogo()) // FIXED: Include logo when creating new profile
                        .updatedAt(LocalDateTime.now())
                        .build();

                employerProfileRepository.save(newProfile);
                return StatusList.Created;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return StatusList.Internal_Server_Error;
        }
    }

    @Override
    public int updateCompanyLogo(String email, String companyLogoUrl) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return StatusList.Not_Found;
            }

            Optional<EmployerProfile> employerProfileOpt = employerProfileRepository.findByUser(user);

            if (employerProfileOpt.isPresent()) {
                EmployerProfile employerProfile = employerProfileOpt.get();
                employerProfile.setCompanyLogo(companyLogoUrl);
                employerProfile.setUpdatedAt(LocalDateTime.now());
                employerProfileRepository.save(employerProfile);
                return StatusList.OK;
            } else {
                // Create a basic profile with just the logo if no profile exists
                EmployerProfile newProfile = EmployerProfile.builder()
                        .user(user)
                        .companyLogo(companyLogoUrl)
                        .updatedAt(LocalDateTime.now())
                        .build();

                employerProfileRepository.save(newProfile);
                return StatusList.OK;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return StatusList.Internal_Server_Error;
        }
    }

    @Override
    public int deleteEmployerProfile(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return StatusList.Not_Found;
            }

            Optional<EmployerProfile> employerProfileOpt = employerProfileRepository.findByUser(user);

            if (employerProfileOpt.isPresent()) {
                employerProfileRepository.delete(employerProfileOpt.get());
                return StatusList.OK;
            } else {
                return StatusList.Not_Found;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return StatusList.Internal_Server_Error;
        }
    }

    @Override
    public boolean employerProfileExists(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return false;
            }

            return employerProfileRepository.findByUser(user).isPresent();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}