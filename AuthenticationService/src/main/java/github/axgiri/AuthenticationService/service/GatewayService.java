package github.axgiri.AuthenticationService.service;

import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.requests.AuthRequest;
import github.axgiri.AuthenticationService.security.Middleware;
import github.axgiri.AuthenticationService.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GatewayService {

    private final UserCompanyService userCompanyService;
    private final Middleware middleware;
    private final TokenService tokenService;

    public boolean checkIsMember(AuthRequest authRequest) {
        String email = tokenService.extractUsername(authRequest.getToken());
        log.info("is member email: {}", email);
        return middleware.isCompanyMember(email, authRequest.getCompanyId());
    }

    public boolean checkIsModerator(AuthRequest authRequest) {
        String email = tokenService.extractUsername(authRequest.getToken());
        log.info("is moderator email: {}", email);
        return middleware.isCompanyModerator(email, authRequest.getCompanyId());
    };

    public boolean checkIsAdmin(AuthRequest authRequest) {
        String email = tokenService.extractUsername(authRequest.getToken());
        log.info("is admin email: {}", email);
        return middleware.isCompanyAdmin(email, authRequest.getCompanyId());
    }

    public boolean checkIsSameUser(AuthRequest authRequest) {
        String email = tokenService.extractUsername(authRequest.getToken());
        log.info("is same user email: {}", email);
        return middleware.isSameUser(email, authRequest.getCreatedBy());
    }

    public boolean checkTokenAndCompany(String token) {
        log.info("validating token: {}", token);
        return userCompanyService.validate(token);
    }
}
