package axgiri.github.Gateway.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String token;
    private Long companyId;
    private Long taskId;
    private Long createdBy;
    private String role;
}
