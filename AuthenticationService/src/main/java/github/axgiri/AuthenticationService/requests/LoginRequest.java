package github.axgiri.AuthenticationService.requests;

import lombok.Data;

@Data
public class LoginRequest {
    String email;
    String password;
}
