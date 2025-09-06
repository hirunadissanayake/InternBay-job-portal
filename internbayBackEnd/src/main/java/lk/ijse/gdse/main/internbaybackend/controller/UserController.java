// UserController.java
package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.AuthaDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.dto.UserDTO;
import lk.ijse.gdse.main.internbaybackend.service.UserService;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                    // Get the complete user details after registration
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
                // Get complete user details
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
}