package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.*;
import lk.ijse.gdse.main.internbaybackend.service.UserService;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponsDto> register(@RequestBody UserDTO userDTO) {
        try {
            int res = userService.saveUser(userDTO);

            switch (res) {
                case StatusList.Created:
                    UserDTO registeredUser = userService.loadUserDetailsByUsername(userDTO.getEmail());
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponsDto(StatusList.Created, "Registration successful", registeredUser));

                case StatusList.Not_Acceptable:
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponsDto(StatusList.Not_Acceptable, "User already exists", null));

                default:
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponsDto(StatusList.Bad_Gateway, "Registration failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponsDto> login(@RequestBody UserDTO userDTO) {
        try {
            int res = userService.loginUser(userDTO);

            if (res == StatusList.Created) {
                UserDTO completeUser = userService.loadUserDetailsByUsername(userDTO.getEmail());
                String token = jwtUtil.generateToken(completeUser);

                AuthaDTO authaDTO = new AuthaDTO(token, completeUser.getEmail(),
                        completeUser.getRole() != null ? completeUser.getRole().toString() : null);

                return ResponseEntity.ok(new ResponsDto(StatusList.Created, "Login successful", authaDTO));
            }
            else if (res == StatusList.Unauthorized) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponsDto(StatusList.Unauthorized, "Invalid password", null));
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponsDto(StatusList.Not_Acceptable, "Email not found", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Helper method to extract email from JWT token
    private String extractEmailFromToken(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            return jwtUtil.getUsernameFromToken(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid token");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponsDto> getCurrentUserProfile(@RequestHeader("Authorization") String token) {
        try {
            String email = extractEmailFromToken(token);

            UserDTO user = userService.loadUserDetailsByUsername(email);
            if (user != null) {
                user.setPasswordHash(null);
                // Use actual HTTP status code instead of StatusList constant
                return ResponseEntity.ok(new ResponsDto(200, "Profile retrieved successfully", user));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(404, "User not found", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(500, e.getMessage(), null));
        }
    }

    // Update user profile
    @PutMapping("/profile")
    public ResponseEntity<ResponsDto> updateProfile(@RequestHeader("Authorization") String token,
                                                    @RequestBody UserDTO userDTO) {
        try {
            String email = extractEmailFromToken(token);

            // Set the email from token to ensure user can only update their own profile
            userDTO.setEmail(email);

            int res = userService.updateUserProfile(userDTO);

            if (res == StatusList.OK) {
                UserDTO updatedUser = userService.loadUserDetailsByUsername(email);
                updatedUser.setPasswordHash(null); // Don't send password hash
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Profile updated successfully", updatedUser));
            } else if (res == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "User not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Profile update failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Update profile picture
    @PutMapping("/profile/picture")
    public ResponseEntity<ResponsDto> updateProfilePicture(@RequestHeader("Authorization") String token,
                                                           @RequestBody ProfilePictureDTO profilePictureDTO) {
        try {
            String email = extractEmailFromToken(token);

            int res = userService.updateProfilePicture(email, profilePictureDTO.getProfilePicUrl());

            if (res == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Profile picture updated successfully", null));
            } else if (res == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "User not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Profile picture update failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Update resume
    @PutMapping("/profile/resume")
    public ResponseEntity<ResponsDto> updateResume(@RequestHeader("Authorization") String token,
                                                   @RequestBody ResumeDTO resumeDTO) {
        try {
            String email = extractEmailFromToken(token);

            int res = userService.updateResume(email, resumeDTO.getResumeUrl());

            if (res == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Resume updated successfully", null));
            } else if (res == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "User not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Resume update failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Change password
    @PutMapping("/profile/password")
    public ResponseEntity<ResponsDto> changePassword(@RequestHeader("Authorization") String token,
                                                     @RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            String email = extractEmailFromToken(token);

            int res = userService.changePassword(email, passwordChangeDTO);

            if (res == StatusList.OK) {
                return ResponseEntity.ok(new ResponsDto(StatusList.OK, "Password changed successfully", null));
            } else if (res == StatusList.Unauthorized) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponsDto(StatusList.Unauthorized, "Current password is incorrect", null));
            } else if (res == StatusList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsDto(StatusList.Not_Found, "User not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsDto(StatusList.Bad_Request, "Password change failed", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error, e.getMessage(), null));
        }
    }
}