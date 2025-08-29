package lk.ijse.gdse.main.internbaybackend.dto;

import lk.ijse.gdse.main.internbaybackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private User.Role role;
    private String phone;
    private String profilePic;
    private String resume;
}