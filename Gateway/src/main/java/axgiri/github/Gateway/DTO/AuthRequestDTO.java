package axgiri.github.Gateway.DTO;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String token;
    private Long companyId;
    private String routeRole;
}
