package github.axgiri.AuthenticationService.requests;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.model.Company;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyRequest {
    
    private Long id;

    @NotNull(message = "name is required")
    private String name;

    private PlanEnum plan;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate subscriptionExpiration;

    private boolean active;

    public Company toEntity() {
        return new Company()
            .setId(this.id)
            .setName(this.name)
            .setPlan(this.plan)
            .setSubscriptionExpiration(this.subscriptionExpiration)
            .setActive(this.active);
    }
}
