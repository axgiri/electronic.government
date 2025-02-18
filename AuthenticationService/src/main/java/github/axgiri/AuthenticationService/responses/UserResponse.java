package github.axgiri.AuthenticationService.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.model.User;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponse {
    
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("name")
    private String name;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("role")
    private RoleEnum role;

    @JsonProperty("company_id")
    private Long companyId;

    public static UserResponse fromEntityToDTO(User user) {
        return new UserResponse()
            .setId(user.getId())
            .setEmail(user.getEmail())
            .setPassword(user.getPassword())
            .setName(user.getName())
            .setSurname(user.getSurname())
            .setRole(user.getRole())
            .setCompanyId(user.getCompany() != null ? user.getCompany().getId() : null);
    }
}
