package github.axgiri.AuthenticationService.responses;

import github.axgiri.AuthenticationService.model.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthResponse {

    private final String token;
    private final User user;
}
