package github.axgiri.AuthenticationService.DTO;

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
public class UserDTO {

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

    @NotNull(message = "role is required")
    private RoleEnum role;

    private Long companyId;

    public User toEntity(Company company) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setSurname(surname);
        user.setRole(role);
        user.setCompany(company != null ? company : null);
        return user;
    }

    public static UserDTO fromEntityToDTO(User user) {
        return new UserDTO(
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
