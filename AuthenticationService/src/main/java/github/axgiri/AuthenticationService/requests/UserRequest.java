package github.axgiri.AuthenticationService.requests;

import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setSurname(surname);
        user.setRole(role != null ? role : RoleEnum.WORKER);
        user.setCompany(company != null ? company : null);
        return user;
    }

    public static UserRequest fromEntityToDTO(User user) {
        return new UserRequest(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.getName(),
            user.getSurname(),
            user.getRole(),
            user.getCompany() != null ? user.getCompany().getId() : null
        );
    }
}
