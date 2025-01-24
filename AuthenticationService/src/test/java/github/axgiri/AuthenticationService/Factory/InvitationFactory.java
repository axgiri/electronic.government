package github.axgiri.AuthenticationService.Factory;

import java.time.LocalDate;
import java.util.UUID;

import com.github.javafaker.Faker;

import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.requests.CompanyRequest;
import github.axgiri.AuthenticationService.requests.InvitationRequest;
import github.axgiri.AuthenticationService.Model.Invitation;

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

    public static InvitationRequest createDTO(Company company, int validityDays) {
        Invitation invitation = create(company, validityDays);
        return InvitationRequest.fromEntityToDTO(invitation);
    }

    public static InvitationRequest createDTOFromEntity(Invitation invitation) {
        return InvitationRequest.fromEntityToDTO(invitation);
    }
}
