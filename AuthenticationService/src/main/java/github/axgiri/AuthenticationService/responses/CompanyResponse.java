package github.axgiri.AuthenticationService.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.model.Company;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class CompanyResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("plan")
    private PlanEnum plan;

    @JsonProperty("subscription_expiration")
    private LocalDate subscriptionExpiration;

    @JsonProperty("active")
    private Boolean active;

    public static CompanyResponse fromEntityToDTO(Company company) {
        return new CompanyResponse()
            .setId(company.getId())
            .setName(company.getName())
            .setPlan(company.getPlan())
            .setSubscriptionExpiration(company.getSubscriptionExpiration())
            .setActive(company.isActive());
    }
}