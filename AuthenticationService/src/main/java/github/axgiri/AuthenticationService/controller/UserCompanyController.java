package github.axgiri.AuthenticationService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.axgiri.AuthenticationService.requests.CompanyRequest;
import github.axgiri.AuthenticationService.responses.CompanyResponse;
import github.axgiri.AuthenticationService.responses.UserResponse;
import github.axgiri.AuthenticationService.service.UserCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users/companies")
@RequiredArgsConstructor
public class UserCompanyController {
    
    private final UserCompanyService service;

    @PreAuthorize("@middleware.isCompanyModerator(principal.username, #companyId)")
    @GetMapping("/inviteUser")
    public ResponseEntity<String> createInvitationLink(@RequestParam Long companyId, @RequestParam int TTL) {
        log.info("creating invitation link for company: {}", companyId);
        String result = "http://localhost:8081/api/users/companies/addToCompany/" + service.createInvitationLink(companyId, TTL);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/addToCompany/{code}")
    public ResponseEntity<UserResponse> addToCompany(@PathVariable String code, @RequestHeader("Authorization") String token) {
        log.info("adding user to company with token: {}", token);
        token = token.substring(7);
        return ResponseEntity.ok(UserResponse.fromEntityToDTO(service.addUserToCompanyByLink(code, token)));
    }

    @GetMapping("/validate/{companyId}")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {
        log.info("request to validate token: {}", token);
        token = token.substring(7);
        service.validate(token);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/createCompany")
    public ResponseEntity<CompanyResponse> create(@RequestBody @Valid CompanyRequest companyRequest, @RequestHeader("Authorization") String token) {
        log.info("creating company with data: {}", companyRequest);
        token = token.substring(7);
        return ResponseEntity.ok(CompanyResponse.fromEntityToDTO(service.createCompanyAddAdmin(companyRequest.toEntity(), token)));
    }
}
