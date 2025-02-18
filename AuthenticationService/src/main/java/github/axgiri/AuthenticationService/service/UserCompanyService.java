package github.axgiri.AuthenticationService.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.model.Invitation;
import github.axgiri.AuthenticationService.model.User;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCompanyService {

    private final CompanyService companyService;
    private final InvitationService invitationService;
    private final UserService userService;
    private final TokenService tokenService;

    public String createInvitationLink(Long companyId, int validityDays) {
        log.info("creating invitation link for company: {}", companyId);
        Company company = companyService.getById(companyId);
        return invitationService.create(company, validityDays).getCode();
    }

    public User addUserToCompanyByLink(String code, String token) {
        log.info("adding user to company with data: {}");
        Boolean isValidCode = invitationService.validate(code);
        if (isValidCode == false) {
            throw(new RuntimeException("invalid link or company"));
        }
        Invitation invitation = invitationService.getByCode(code);
        String email = tokenService.extractUsername(token);
        User user = userService.getByEmail(email);
        user.setCompany(invitation.getCompany());
        userService.update(user, user.getId());
        invitationService.delete(invitation);
        return user;
    }

    @Cacheable(value = "isCompanyAndTokenValid", key="'isToken_' + #token + '_AndCompany_Valid'")
    public Boolean validate(String token) {
        userService.validateToken(token);
        String email = tokenService.extractUsername(token);
        User user = userService.getByEmail(email);
        if (companyService.isActive(user.getCompany().getId()) == true) {
            return true;
        } else {
            return false;
        }
    }

    public Company createCompanyAddAdmin(Company company, String token) {
        log.info("creating company with data: {}", company);
        String email = tokenService.extractUsername(token);
        User user = userService.getByEmail(email);
        user.setCompany(companyService.add(company));
        user.setRole(RoleEnum.ADMIN);
        userService.update(user, user.getId());
        return company;
    }    
}
