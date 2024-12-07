package github.axgiri.AuthenticationService.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.axgiri.AuthenticationService.DTO.UserDTO;
import github.axgiri.AuthenticationService.Service.UserCompanyService;

@RestController
@RequestMapping("/api/users/companies")
public class UserCompanyController {
    private static final Logger logger = LoggerFactory.getLogger(UserCompanyController.class);
    private final UserCompanyService service;

    public UserCompanyController(UserCompanyService service){
        this.service = service;
    }

    @GetMapping("/inviteUser")
    public ResponseEntity<String> createInvitationLink(@RequestParam Long companyId, @RequestParam int TTL){
        logger.info("creating invitation link for company: {}", companyId);
        String result = "http://localhost:8081/api/users/companies/addToCompany/" + service.createInvitationLink(companyId, TTL);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/addToCompany/{code}")
    public ResponseEntity<UserDTO> addToCompany(@PathVariable String code, @RequestBody UserDTO userDTO){
        logger.info("adding user to company with data: {}", userDTO);
        return ResponseEntity.ok(service.addUserToCompanyByLink(code, userDTO));
    }
}
