package github.axgiri.AuthenticationService.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.requests.LoginRequest;
import github.axgiri.AuthenticationService.responses.AuthResponse;
import github.axgiri.AuthenticationService.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.model.User;
import github.axgiri.AuthenticationService.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Cacheable(value = "companyMembers", key = "'company_' + #id + '_Members'")
    public List<User> getByCompanyId(Long id) {
        log.info("fetching users by company id: {}", id);
        return repository.findByCompanyId(id)
            .stream()
            .collect(Collectors.toList());
    }

    @Cacheable(value = "fourHoursCache", key="'userWithId_' + #id")
    public User getById(Long id) {
        log.info("fetching user by id: {}", id);
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id: " + id + " not found"));
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = repository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User with email: " + request.getEmail() + " not found"));
        String token = tokenService.generateToken(user);
        return new AuthResponse(token, user);
    }

    public User add(User user) {
        log.info("adding new user: {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public User update(User user, Long id) {
        log.info("updating user: {}", user);
        User existUser = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id: " + id + " not found"));
            existUser.setEmail(user.getEmail());
            existUser.setPassword(user.getPassword());
            existUser.setName(user.getName());
            existUser.setSurname(user.getSurname());
            existUser.setCompany(user.getCompany() != null ? companyService.getById(user.getCompany().getId()) : null);
        return repository.save(user);
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public void delete(Long id) {
        log.info("deleting user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        repository.delete(user);
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public void deleteCompanyId(Long id) {
        log.info("deleting company from user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        user.setCompany(null);
        setRole(id, RoleEnum.WORKER);
        repository.save(user);
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public void setRole(Long id, RoleEnum role) {
        log.info("setting role for user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        user.setRole(role);
        repository.save(user);
    }

    @Cacheable(value = "oneHourCache", key = "'isToken' + #token + '_Valid'")
    public boolean validateToken(String token) {
        if (!tokenService.isTokenValid(token, repository.findByEmail(tokenService.extractUsername(token)).orElseThrow())) {
            throw new RuntimeException("token is invalid");
        }
        return true;
    }

    public User getByEmail(String email) {
        log.info("fetching user by email: {}", email);
        return repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("user with email " + email + "not found"));
    }
}
