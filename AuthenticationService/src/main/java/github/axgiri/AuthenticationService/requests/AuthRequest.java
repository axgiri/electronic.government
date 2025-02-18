package github.axgiri.AuthenticationService.requests;

import lombok.Data;

@Data
public class AuthRequest {
    private String token;
    private Long companyId;
    private Long createdBy;
    private String routeRole;
}
