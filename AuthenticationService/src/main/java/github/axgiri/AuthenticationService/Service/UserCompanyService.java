package github.axgiri.AuthenticationService.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.DTO.InvitationDTO;
import github.axgiri.AuthenticationService.DTO.UserDTO;
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

    public String createInvitationLink(Long companyId, int validityDays){
        logger.info("creating invitation link for company: {}", companyId);
        CompanyDTO companyDTO = companyService.getById(companyId);
        InvitationDTO invitationDTO = invitationService.create(companyDTO, validityDays);
        return invitationDTO.getCode();
    }

    public UserDTO addUserToCompanyByLink(String code, String token){
        logger.info("adding user to company with data: {}");
        Boolean isValidCode = invitationService.validate(code);
        if (isValidCode == false) {
            throw(new RuntimeException("invalid link or company"));
        }
        InvitationDTO invitationDTO = invitationService.getByCode(code);
        String email = tokenService.extractUsername(token);
        UserDTO userDTO = userService.getByEmail(email);
        userDTO.setCompanyId(invitationDTO.getCompanyId());
        userService.update(userDTO, userDTO.getId());
        invitationService.delete(invitationDTO);
        return userDTO;
    }

    public Boolean validate(String token, Long companyId){
        userService.validateToken(token);
        if (companyService.isActive(companyId) == true){
            return true;
        } else {
            return false;
        }
    }

    public CompanyDTO add(CompanyDTO companyDTO, String token){
        logger.info("creating company with data: {}", companyDTO);
        String email = tokenService.extractUsername(token);
        UserDTO userDTO = userService.getByEmail(email);
        userDTO.setCompanyId(companyDTO.getId());
        userDTO.setRole(RoleEnum.ADMIN);
        companyService.add(companyDTO);
        userService.update(userDTO, userDTO.getId());
        return companyDTO;
    }   
}
