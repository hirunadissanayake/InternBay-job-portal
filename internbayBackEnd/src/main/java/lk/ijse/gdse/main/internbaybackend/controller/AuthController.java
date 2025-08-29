package lk.ijse.gdse.main.internbaybackend.controller;



import lk.ijse.gdse.main.internbaybackend.dto.AuthaDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.dto.UserDTO;
import lk.ijse.gdse.main.internbaybackend.service.impl.UserServiceImpl;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final ResponsDto responseDTO;

    //constructor injection
    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserServiceImpl userService, ResponsDto responseDTO, UserServiceImpl userService1, ResponsDto responseDTO1) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;

        this.userService = userService1;
        this.responseDTO = responseDTO1;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponsDto> authenticate(@RequestBody UserDTO userDTO) {
        System.out.println("jjjj");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPasswordHash()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponsDto(StatusList.Unauthorized, "Invalid Credentials", e.getMessage()));
        }

        UserDTO loadedUser = userService.loadUserDetailsByUsername(userDTO.getEmail());
        if (loadedUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponsDto(StatusList.Conflict, "Authorization Failure! Please Try Again", null));
        }

        String token = jwtUtil.generateToken(loadedUser);
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponsDto(StatusList.Conflict, "Authorization Failure! Please Try Again", null));
        }

        AuthaDTO authDTO = new AuthaDTO();
        authDTO.setEmail(loadedUser.getEmail());
        authDTO.setToken(token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponsDto(StatusList.Created, "Success", authDTO));
    }

}