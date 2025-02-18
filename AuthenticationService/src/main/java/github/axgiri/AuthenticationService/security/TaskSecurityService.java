package github.axgiri.AuthenticationService.security;

import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.requests.AuthRequest;
import github.axgiri.AuthenticationService.service.GatewayService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TaskSecurityService {

    private final GatewayService gatewayService;

    public boolean validateRequest(AuthRequest requestDTO) {
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
