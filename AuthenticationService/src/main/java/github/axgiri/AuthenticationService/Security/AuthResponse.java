package github.axgiri.AuthenticationService.Security;

import github.axgiri.AuthenticationService.requests.UserRequest;
import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private UserRequest user;

    public AuthResponse(String token, UserRequest user) {
        this.token = token;
        this.user = user;
    }
}
