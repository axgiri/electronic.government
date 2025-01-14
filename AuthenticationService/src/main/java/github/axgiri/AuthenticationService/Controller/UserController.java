package github.axgiri.AuthenticationService.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import github.axgiri.AuthenticationService.DTO.LoginRequest;
import github.axgiri.AuthenticationService.DTO.UserDTO;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.Security.AuthResponse;
import github.axgiri.AuthenticationService.Service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PreAuthorize("@middleware.isCompanyMember(principal.username, #id)")
    @GetMapping("/getByCompanyId/{id}")
    public ResponseEntity<List<UserDTO>> getByCompanyId(@PathVariable Long id) {
        logger.info("fetching all user with company id: {}", id);
        return ResponseEntity.ok(service.getByCompanyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        logger.info("fetching user by id: {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = service.authenticate(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<AuthResponse> create(@Valid @RequestBody UserDTO userDTO) {
        AuthResponse authResponse = service.add(userDTO);
        return ResponseEntity.ok(authResponse);
    }

    @PreAuthorize("@middleware.isSameUser(principal.username, #id)")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> update(@RequestBody UserDTO userDTO, @PathVariable Long id) {
        logger.info("updating user: {}", userDTO);
        return ResponseEntity.ok(service.update(userDTO, id));
    }

    @PreAuthorize("@middleware.isSameUser(principal.username, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("deleting user with id: {}", id);
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@middleware.isSameUser(principal.username, #id) || @middleware.isCompanyModeratorForUser(principal.username, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<Void> outFromCompany(@PathVariable Long id) {
        logger.info("deleting company from user with id: {}", id);
        service.deleteCompanyId(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@middleware.isCompanyModeratorForUser(principal.username, #id)")
    @PostMapping("/setRole/{id}")
    public ResponseEntity<Void> setRole(@PathVariable Long id, @RequestBody RoleEnum role) {
        logger.info("setting role for user with id: {}", id);
        service.setRole(id, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {
        logger.info("request to validate token: {}", token);
        token = token.substring(7);
        service.validateToken(token);
        return ResponseEntity.ok(true);
    }
}
