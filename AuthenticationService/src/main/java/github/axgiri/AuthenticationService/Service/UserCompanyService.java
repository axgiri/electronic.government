package github.axgiri.AuthenticationService.Service;

import github.axgiri.AuthenticationService.Model.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.requests.CompanyRequest;
import github.axgiri.AuthenticationService.requests.InvitationRequest;
import github.axgiri.AuthenticationService.requests.UserRequest;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.Security.TokenService;

@Service
public class UserCompanyService {

    private static final Logger logger = LoggerFactory.getLogger(UserCompanyService.class);
    private final CompanyService companyService;
    private final InvitationService invitationService;
    private final UserService userService;
    private final TokenService tokenService;

    public UserCompanyService(CompanyService companyService, InvitationService invitationService, UserService userService, TokenService tokenService) {
        this.invitationService = invitationService;
        this.companyService = companyService;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    public String createInvitationLink(Long companyId, int validityDays) {
        logger.info("creating invitation link for company: {}", companyId);
        Company company = companyService.getById(companyId);
        InvitationRequest invitationDTO = invitationService.create(company, validityDays);
        return invitationDTO.getCode();
    }

    public UserRequest addUserToCompanyByLink(String code, String token) {
        logger.info("adding user to company with data: {}");
        Boolean isValidCode = invitationService.validate(code);
        if (isValidCode == false) {
            throw(new RuntimeException("invalid link or company"));
        }
        InvitationRequest invitationDTO = invitationService.getByCode(code);
        String email = tokenService.extractUsername(token);
        UserRequest userDTO = userService.getByEmail(email);
        userDTO.setCompanyId(invitationDTO.getCompanyId());
        userService.update(userDTO, userDTO.getId());
        invitationService.delete(invitationDTO);
        return userDTO;
    }

    @Cacheable(value = "isCompanyAndTokenValid", key="'isToken_' + #token + '_AndCompany_Valid'")
    public Boolean validate(String token) {
        userService.validateToken(token);
        String email = tokenService.extractUsername(token);
        UserRequest userDTO = userService.getByEmail(email);
        if (companyService.isActive(userDTO.getCompanyId()) == true) {
            return true;
        } else {
            return false;
        }
    }

    public CompanyRequest createCompanyAddAdmin(CompanyRequest companyDTO, String token) {

        logger.info("creating company with data: {}", companyDTO);
        String email = tokenService.extractUsername(token);
        UserRequest userDTO = userService.getByEmail(email);
        CompanyRequest company = companyService.add(companyDTO);
        userDTO.setCompanyId(company.getId());
        userDTO.setRole(RoleEnum.ADMIN);
        userService.update(userDTO, userDTO.getId());

        return company;
    }    
}
