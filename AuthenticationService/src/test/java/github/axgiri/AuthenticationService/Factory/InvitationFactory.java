package github.axgiri.AuthenticationService.Factory;

import java.time.LocalDate;
import java.util.UUID;

import com.github.javafaker.Faker;

import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.model.Invitation;

public class InvitationFactory {
    
    private static final Faker faker = new Faker();

    public static Invitation create(Company company, int validityDays) {
        Invitation invitation = new Invitation();
        invitation.setId(faker.number().randomNumber());
        invitation.setCode(UUID.randomUUID().toString());
        invitation.setCompany(company);
        invitation.setCreatedAt(LocalDate.now());
        invitation.setExpiresAt(LocalDate.now().plusDays(validityDays));
        return invitation;
    }
}
