package github.axgiri.AuthenticationService.Factory;

import com.github.javafaker.Faker;

import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.model.User;

public class UserFactory {
    
    private static final Faker faker = new Faker();

    public static User create() {
        User user = new User();
        user.setId(faker.number().randomNumber());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.dog().name() + faker.cat().name());
        user.setName(faker.name().toString());
        user.setSurname(faker.name().lastName());
        user.setRole(RoleEnum.WORKER);
        user.setCompany(null);
        return user;
    }
}
