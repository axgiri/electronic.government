package github.axgiri.AuthenticationService.Security;

import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.DTO.AuthRequestDTO;
import github.axgiri.AuthenticationService.Service.GatewayService;

@Service
public class TaskSecurityService {

    private GatewayService gatewayService;

    private TaskSecurityService(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    public boolean validateRequest(AuthRequestDTO requestDTO) {
        switch (requestDTO.getRouteRole()) {
            case "user":
                return gatewayService.checkTokenAndCompany(requestDTO.getToken())
                        && gatewayService.checkIsMember(requestDTO);
            case "moderator":
                return gatewayService.checkTokenAndCompany(requestDTO.getToken())
                        && gatewayService.checkIsModerator(requestDTO);
            case "admin":
                return gatewayService.checkTokenAndCompany(requestDTO.getToken())
                        && gatewayService.checkIsAdmin(requestDTO);
            default:
                throw new RuntimeException("failed to parse role from JSON to AuthRequestDTO");
        }
    }
}
