package github.axgiri.AuthenticationService.DTO;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String token;
    private Long companyId;
    private String routeRole;
}
