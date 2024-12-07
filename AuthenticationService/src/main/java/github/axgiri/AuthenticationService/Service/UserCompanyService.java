package github.axgiri.AuthenticationService.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.DTO.InvitationDTO;
import github.axgiri.AuthenticationService.DTO.UserDTO;

@Service
public class UserCompanyService {

    private static final Logger logger = LoggerFactory.getLogger(UserCompanyService.class);
    private final CompanyService companyService;
    private final InvitationService invitationService;
    private final UserService userService;

    public UserCompanyService(CompanyService companyService, InvitationService invitationService, UserService userService) {
        this.invitationService = invitationService;
        this.companyService = companyService;
        this.userService = userService;
    }

    public String createInvitationLink(Long companyId, int validityDays){
        logger.info("creating invitation link for company: {}", companyId);
        CompanyDTO companyDTO = companyService.getById(companyId);
        InvitationDTO invitationDTO = invitationService.create(companyDTO, validityDays);
        return invitationDTO.getCode();
    }

    public UserDTO addUserToCompanyByLink(String code, UserDTO userDTO){ //todo we should get userDTO.getId() from token
        logger.info("adding user to company with data: {}", userDTO);
        Boolean isValidCode = invitationService.validate(code);
        if (isValidCode == false) {
            throw(new RuntimeException("invalid link or company"));
        }

        InvitationDTO invitationDTO = invitationService.getByCode(code);
        userDTO.setCompanyId(invitationDTO.getCompanyId());
        userService.update(userDTO, userDTO.getId());
        invitationService.delete(invitationDTO);
        return userDTO;
    }
}

    //TODO:
    //userService.validate() token cache for 12h
    //companyService.isActive() check status cache for 6h
    //do command select where company active is true, check localTime > expiration change active to false
    //do command to close tokens that not active by time
