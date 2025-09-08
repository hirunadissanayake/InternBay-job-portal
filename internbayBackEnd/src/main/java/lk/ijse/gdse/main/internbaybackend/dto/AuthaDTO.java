package lk.ijse.gdse.main.internbaybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AuthaDTO {
    private String token;
    private String email;
    private String role;


}