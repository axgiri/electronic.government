package github.axgiri.AuthenticationService.requests;

import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {

    private Long id;

    @NotNull(message = "email is required")
    @Email
    private String email;

    @NotNull(message = "password is required")
    private String password;

    @NotNull(message = "name is required")
    private String name;

    @NotNull(message = "surname is required")
    private String surname;

    private RoleEnum role;

    private Long companyId;

    public User toEntity(Company company) {
        return new User()
            .setId(this.id)
            .setEmail(this.email)
            .setPassword(this.password)
            .setName(this.name)
            .setSurname(this.surname)
            .setRole(this.role != null ? role : RoleEnum.WORKER)
            .setCompany(company != null ? company : null);
    }
}
