package github.axgiri.AuthenticationService.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import github.axgiri.AuthenticationService.security.TokenService;
import github.axgiri.AuthenticationService.service.CompanyService;
import github.axgiri.AuthenticationService.service.InvitationService;
import github.axgiri.AuthenticationService.service.UserCompanyService;
import github.axgiri.AuthenticationService.service.UserService;
import github.axgiri.AuthenticationService.Factory.CompanyFactory;
import github.axgiri.AuthenticationService.Factory.InvitationFactory;
import github.axgiri.AuthenticationService.Factory.UserFactory;
import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.model.Invitation;
import github.axgiri.AuthenticationService.model.User;

@ExtendWith(MockitoExtension.class)
public class UserCompanyServiceTest {

    private static final Faker faker = new Faker();
    
    @InjectMocks
    private UserCompanyService service;

    @Mock
    private CompanyService companyService;
    
    @Mock
    private InvitationService invitationService;
    
    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Test
    public void createInvitationLinkTest() {
        Company company = CompanyFactory.create();
        int validityDays = faker.number().numberBetween(1, 30);
        Invitation invitation = InvitationFactory.create(company, validityDays);
        when(companyService.getById(company.getId())).thenReturn(company);
        when(invitationService.create(company, validityDays)).thenReturn(invitation);
        String result = service.createInvitationLink(company.getId(), validityDays);
        assertEquals(invitation.getCode(), result);
        verify(companyService, times(1)).getById(company.getId());
        verify(invitationService, times(1)).create(company, validityDays);
    }

    @Test
    public void createInvitationLinkCompanyNotFoundTest() {
        Long companyId = faker.number().randomNumber();
        int validityDays = faker.number().numberBetween(1, 30);
        when(companyService.getById(companyId)).thenThrow(new RuntimeException("comopany with id: " + companyId + " not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.createInvitationLink(companyId, validityDays);
        });

        assertEquals("comopany with id: " + companyId + " not found", exception.getMessage());
        verify(companyService, times(1)).getById(companyId);
        verify(invitationService, never()).create(any(), anyInt());
    }

    @Test
    public void addUserToCompanyByLinkTest() {
        Company company = CompanyFactory.create();
        String token = faker.name().toString();
        String code = UUID.randomUUID().toString();
        Invitation invitation = InvitationFactory.create(company, faker.number().numberBetween(1, 30));
        User user = UserFactory.create();
        when(invitationService.validate(code)).thenReturn(true);
        when(invitationService.getByCode(code)).thenReturn(invitation);
        when(tokenService.extractUsername(token)).thenReturn(user.getEmail());
        when(userService.getByEmail(user.getEmail())).thenReturn(user);
        when(userService.update(user, user.getId())).thenReturn(user);
        User result = service.addUserToCompanyByLink(code, token);
        assertEquals(user, result);
        assertEquals(invitation.getCompany(), user.getCompany());
        verify(invitationService, times(1)).validate(code);
        verify(invitationService, times(1)).getByCode(code);
        verify(tokenService, times(1)).extractUsername(token);
        verify(userService, times(1)).getByEmail(user.getEmail());
        verify(userService, times(1)).update(user, user.getId());
        verify(invitationService, times(1)).delete(invitation);
    }

    @Test
    public void testAddUserToCompanyByLink_InvalidCode() {
        String token = faker.name().toString();
        String code = UUID.randomUUID().toString();
        when(invitationService.validate(code)).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.addUserToCompanyByLink(code, token);
        });
        assertEquals("invalid link or company", exception.getMessage());
        verify(invitationService, times(1)).validate(code);
        verify(invitationService, never()).getByCode(anyString());
        verify(tokenService, never()).extractUsername(anyString());
        verify(userService, never()).getByEmail(anyString());
        verify(userService, never()).update(any(), anyLong());
        verify(invitationService, never()).delete(any());
    }
}
