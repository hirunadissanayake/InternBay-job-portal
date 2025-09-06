package lk.ijse.gdse.main.internbaybackend.service;

import lk.ijse.gdse.main.internbaybackend.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    int saveUser(UserDTO userDTO);

    UserDTO loadUserDetailsByUsername(String username);

    boolean ifEmailExists(String email);

    UserDTO searchUser(String email);

    int resetPass(UserDTO exuser);

    String getUserRoleByEmail(String email);

    int loginUser(UserDTO userDTO);
}