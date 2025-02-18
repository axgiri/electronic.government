package github.axgiri.AuthenticationService.controller;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.axgiri.AuthenticationService.requests.LoginRequest;
import github.axgiri.AuthenticationService.requests.UserRequest;
import github.axgiri.AuthenticationService.responses.AuthResponse;
import github.axgiri.AuthenticationService.responses.UserResponse;
import github.axgiri.AuthenticationService.service.CompanyService;
import github.axgiri.AuthenticationService.service.UserService;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.model.User;
import jakarta.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final CompanyService companyService;

    @PreAuthorize("@middleware.isCompanyMember(principal.username, #id)")
    @GetMapping("/getByCompanyId/{id}")
    public ResponseEntity<List<UserResponse>> getByCompanyId(@PathVariable Long id) {
        log.info("fetching all user with company id: {}", id);
        List<User> users = service.getByCompanyId(id);
        List<UserResponse> response = users.stream()
            .map(UserResponse::fromEntityToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        log.info("fetching user by id: {}", id);
        return ResponseEntity.ok(UserResponse.fromEntityToDTO(service.getById(id)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = service.authenticate(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest) {
        Company company = null;
        if (userRequest.getCompanyId() != null) {   
            company = companyService.getById(userRequest.getCompanyId());
        }
        return ResponseEntity.ok(UserResponse.fromEntityToDTO(service.add(userRequest.toEntity(company))));
    }

    @PreAuthorize("@middleware.isSameUser(principal.username, #id)")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest userRequest, @PathVariable Long id) {
        log.info("updating user: {}", userRequest);
        Company company = null;
        if (userRequest.getCompanyId() != null) {   
            company = companyService.getById(userRequest.getCompanyId());
        }
        company = companyService.getById(userRequest.getCompanyId());
        return ResponseEntity.ok(UserResponse.fromEntityToDTO(service.update(userRequest.toEntity(company), id)));
    }

    @PreAuthorize("@middleware.isSameUser(principal.username, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("deleting user with id: {}", id);
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@middleware.isSameUser(principal.username, #id) || @middleware.isCompanyModeratorForUser(principal.username, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<Void> outFromCompany(@PathVariable Long id) {
        log.info("deleting company from user with id: {}", id);
        service.deleteCompanyId(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@middleware.isCompanyModeratorForUser(principal.username, #id)")
    @PostMapping("/setRole/{id}")
    public ResponseEntity<Void> setRole(@PathVariable Long id, @RequestBody RoleEnum role) {
        log.info("setting role for user with id: {}", id);
        service.setRole(id, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {
        log.info("request to validate token: {}", token);
        token = token.substring(7);
        service.validateToken(token);
        return ResponseEntity.ok(true);
    }
}
