package github.axgiri.AuthenticationService.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.Model.Company;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    
    private Long id;

    @NotNull(message = "name is required")
    private String name;

    private PlanEnum plan;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate subscriptionExpiration;

    private boolean active;

    public Company toEntity(){
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setPlan(plan);
        company.setSubscriptionExpiration(subscriptionExpiration);
        company.setActive(active);
        return company;
    }

    public static CompanyDTO fromEntityToDTO(Company company){
        return new CompanyDTO(
            company.getId(),
            company.getName(),
            company.getPlan(),
            company.getSubscriptionExpiration(),
            company.isActive()
        );
    }
}
