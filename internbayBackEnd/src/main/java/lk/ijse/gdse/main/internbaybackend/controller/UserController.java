package lk.ijse.gdse.main.internbaybackend.controller;



import lk.ijse.gdse.main.internbaybackend.dto.AuthaDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.dto.UserDTO;
import lk.ijse.gdse.main.internbaybackend.service.impl.UserServiceImpl;
import lk.ijse.gdse.main.internbaybackend.util.JwtUtil;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserServiceImpl userServies;

    private final JwtUtil jwtUtil;

    public UserController(UserServiceImpl userServies, JwtUtil jwtUtil) {
        this.userServies = userServies;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponsDto> register(@RequestBody UserDTO userDTO){

        try {
            int res = userServies.saveUser(userDTO);
            String token = jwtUtil.generateToken(userDTO);
            AuthaDTO authaDTO = new AuthaDTO(token,userDTO.getEmail());

            switch (res){
                case StatusList.Created:
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponsDto(StatusList.Created,"success",authaDTO));
                case StatusList.Not_Acceptable:
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponsDto(StatusList.Not_Acceptable,"fail",null));
                default:
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponsDto(StatusList.Bad_Gateway,"fail",null));
            }
        } catch (Exception e) {
            e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsDto(StatusList.Internal_Server_Error,e.getMessage(),null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<ResponsDto> login(@RequestBody UserDTO userDTO) {
        try {
            int res = userServies.loginUser(userDTO);

            if (res == StatusList.Created) {
                String role = userServies.getUserRoleByEmail(userDTO.getEmail());
                String token = jwtUtil.generateToken(userDTO);

                AuthaDTO authaDTO = new AuthaDTO(token, userDTO.getEmail(), role);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponsDto(StatusList.Created, "Login success", authaDTO));
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