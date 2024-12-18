package github.axgiri.AuthenticationService.Factory;

import java.time.LocalDate;
import java.util.UUID;

import com.github.javafaker.Faker;

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.DTO.InvitationDTO;
import github.axgiri.AuthenticationService.Model.Invitation;

public class InvitationFactory {
    
    private static final Faker faker = new Faker();

    public static Invitation create(CompanyDTO companyDTO, int validityDays){
        Invitation invitation = new Invitation();
        invitation.setId(faker.number().randomNumber());
        invitation.setCode(UUID.randomUUID().toString());
        invitation.setCompany(companyDTO.toEntity());
        invitation.setCreatedAt(LocalDate.now());
        invitation.setExpiresAt(LocalDate.now().plusDays(validityDays));
        return invitation;
    }

    public static InvitationDTO createDTO(CompanyDTO companyDTO, int validityDays){
        Invitation invitation = create(companyDTO, validityDays);
        return InvitationDTO.fromEntityToDTO(invitation);
    }

    public static InvitationDTO createDTOFromEntity(Invitation invitation){
        return InvitationDTO.fromEntityToDTO(invitation);
    }
}
