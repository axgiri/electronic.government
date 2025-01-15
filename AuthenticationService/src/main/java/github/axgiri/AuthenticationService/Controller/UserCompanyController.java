package github.axgiri.AuthenticationService.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.DTO.UserDTO;
import github.axgiri.AuthenticationService.Service.UserCompanyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/companies")
public class UserCompanyController {
    private static final Logger logger = LoggerFactory.getLogger(UserCompanyController.class);
    private final UserCompanyService service;

    public UserCompanyController(UserCompanyService service) {
        this.service = service;
    }

    @PreAuthorize("@middleware.isCompanyModerator(principal.username, #companyId)")
    @GetMapping("/inviteUser")
    public ResponseEntity<String> createInvitationLink(@RequestParam Long companyId, @RequestParam int TTL) {
        logger.info("creating invitation link for company: {}", companyId);
        String result = "http://localhost:8081/api/users/companies/addToCompany/" + service.createInvitationLink(companyId, TTL);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/addToCompany/{code}")
    public ResponseEntity<UserDTO> addToCompany(@PathVariable String code, @RequestHeader("Authorization") String token) {
        logger.info("adding user to company with token: {}", token);
        token = token.substring(7);
        return ResponseEntity.ok(service.addUserToCompanyByLink(code, token));
    }

    @GetMapping("/validate/{companyId}")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {
        logger.info("request to validate token: {}", token);
        token = token.substring(7);
        service.validate(token);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/createCompany")
    public ResponseEntity<CompanyDTO> create(@RequestBody @Valid CompanyDTO companyDTO, @RequestHeader("Authorization") String token) {
        logger.info("creating company with data: {}", companyDTO);
        token = token.substring(7);
        return ResponseEntity.ok(service.createCompanyAddAdmin(companyDTO, token));
    }
}
