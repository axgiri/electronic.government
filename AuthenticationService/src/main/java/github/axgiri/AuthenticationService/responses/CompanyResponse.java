package github.axgiri.AuthenticationService.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import github.axgiri.AuthenticationService.Model.Company;

public record CompanyResponse(
    @JsonIgnore Company company
) {
    @JsonProperty("name")
    public String getName() {
        return this.company.getName();
    }
}
