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

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.DTO.InvitationDTO;
import github.axgiri.AuthenticationService.DTO.UserDTO;
import github.axgiri.AuthenticationService.Factory.CompanyFactory;
import github.axgiri.AuthenticationService.Factory.InvitationFactory;
import github.axgiri.AuthenticationService.Factory.UserFactory;
import github.axgiri.AuthenticationService.Security.TokenService;

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
    public void createInvitationLinkTest(){
        CompanyDTO companyDTO = CompanyFactory.createDTO();
        int validityDays = faker.number().numberBetween(1, 30);
        InvitationDTO invitationDTO = InvitationFactory.createDTO(companyDTO, validityDays);
        when(companyService.getById(companyDTO.getId())).thenReturn(companyDTO);
        when(invitationService.create(companyDTO, validityDays)).thenReturn(invitationDTO);
        String result = service.createInvitationLink(companyDTO.getId(), validityDays);
        assertEquals(invitationDTO.getCode(), result);
        verify(companyService, times(1)).getById(companyDTO.getId());
        verify(invitationService, times(1)).create(companyDTO, validityDays);
    }

    @Test
    public void createInvitationLinkCompanyNotFoundTest(){
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
    public void addUserToCompanyByLinkTest(){
        CompanyDTO companyDTO = CompanyFactory.createDTO();
        String token = faker.name().toString();
        String code = UUID.randomUUID().toString();
        InvitationDTO invitationDTO = InvitationFactory.createDTO(companyDTO, faker.number().numberBetween(1, 30));
        UserDTO userDTO = UserFactory.createDTO();
        when(invitationService.validate(code)).thenReturn(true);
        when(invitationService.getByCode(code)).thenReturn(invitationDTO);
        when(tokenService.extractUsername(token)).thenReturn(userDTO.getEmail());
        when(userService.getByEmail(userDTO.getEmail())).thenReturn(userDTO);
        when(userService.update(userDTO, userDTO.getId())).thenReturn(userDTO);
        UserDTO result = service.addUserToCompanyByLink(code, token);
        assertEquals(userDTO, result);
        assertEquals(invitationDTO.getCompanyId(), userDTO.getCompanyId());
        verify(invitationService, times(1)).validate(code);
        verify(invitationService, times(1)).getByCode(code);
        verify(tokenService, times(1)).extractUsername(token);
        verify(userService, times(1)).getByEmail(userDTO.getEmail());
        verify(userService, times(1)).update(userDTO, userDTO.getId());
        verify(invitationService, times(1)).delete(invitationDTO);
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
