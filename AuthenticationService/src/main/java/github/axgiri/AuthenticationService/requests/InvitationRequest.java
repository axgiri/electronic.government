package github.axgiri.AuthenticationService.requests;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Model.Invitation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationRequest {

    private Long id;

    private String code;

    @NotNull(message = "companyId is required")
    private Long companyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expiresAt;

    public Invitation toEntity(Company company) {
        Invitation invitation = new Invitation();
        invitation.setId(this.id);
        invitation.setCode(this.code);
        invitation.setCompany(company);
        invitation.setCreatedAt(this.createdAt);
        invitation.setExpiresAt(this.expiresAt);
        return invitation;
    }

    public static InvitationRequest fromEntityToDTO(Invitation invitation) {
        return new InvitationRequest(
            invitation.getId(),
            invitation.getCode(),
            invitation.getCompany() != null ? invitation.getCompany().getId() : null,
            invitation.getCreatedAt(),
            invitation.getExpiresAt()
        );
    }
}
