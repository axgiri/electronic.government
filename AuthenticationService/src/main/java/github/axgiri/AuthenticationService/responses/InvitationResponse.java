package github.axgiri.AuthenticationService.responses;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import github.axgiri.AuthenticationService.model.Invitation;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InvitationResponse {
    
    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("company_id")
    private Long companyId;

    @JsonProperty("created_at")
    private LocalDate createdAt;

    @JsonProperty("expires_at")
    private LocalDate expiresAt;

    public static InvitationResponse fromEntityToDTO(Invitation invitation) {
        return new InvitationResponse()
            .setId(invitation.getId())
            .setCode(invitation.getCode())
            .setCompanyId(invitation.getCompany() != null ? invitation.getCompany().getId() : null) 
            .setCreatedAt(invitation.getCreatedAt())
            .setExpiresAt(invitation.getExpiresAt());
    }
}
