package github.axgiri.AuthenticationService.Security;

import github.axgiri.AuthenticationService.DTO.UserDTO;
import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private UserDTO user;

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}
