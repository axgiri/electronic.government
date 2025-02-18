package github.axgiri.AuthenticationService.requests;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.model.Invitation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
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
        return new Invitation()
            .setId(this.id)
            .setCode(this.code)
            .setCompany(company)
            .setCreatedAt(this.createdAt)
            .setExpiresAt(this.expiresAt);
    }
}
