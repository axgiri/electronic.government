package github.axgiri.AuthenticationService.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.requests.LoginRequest;
import github.axgiri.AuthenticationService.requests.UserRequest;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Model.User;
import github.axgiri.AuthenticationService.Repository.UserRepository;
import github.axgiri.AuthenticationService.Security.AuthResponse;
import github.axgiri.AuthenticationService.Security.TokenService;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UserService(UserRepository repository, CompanyService companyService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.repository = repository;
        this.companyService = companyService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Cacheable(value = "companyMembers", key = "'company_' + #id + '_Members'")
    public List<UserRequest> getByCompanyId(Long id) {
        logger.info("fetching users by company id: {}", id);
        return repository.findByCompanyId(id)
            .stream()
            .map(UserRequest::fromEntityToDTO)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "fourHoursCache", key="'userWithId_' + #id")
    public UserRequest getById(Long id) {
        logger.info("fetching user by id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id: " + id + " not found"));
        return UserRequest.fromEntityToDTO(user);
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = repository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User with email: " + request.getEmail() + " not found"));
        String token = tokenService.generateToken(user);
        return new AuthResponse(token, UserRequest.fromEntityToDTO(user));
    }

    public AuthResponse add(UserRequest userDTO) {
        logger.info("adding new user: {}", userDTO);
        Company company = null;
        if (userDTO.getCompanyId() != null) {
            company = companyService.getById(userDTO.getCompanyId());
        }
        User user = userDTO.toEntity(company);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
        String token = tokenService.generateToken(user);
        return new AuthResponse(token, UserRequest.fromEntityToDTO(user));
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public UserRequest update(UserRequest userDTO, Long id) {
        logger.info("updating user: {}", userDTO);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id: " + id + " not found"));
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setCompany(userDTO.getCompanyId() != null ? companyService.getById(userDTO.getCompanyId()) : null);
        return UserRequest.fromEntityToDTO(repository.save(user));
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public void delete(Long id) {
        logger.info("deleting user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        repository.delete(user);
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public void deleteCompanyId(Long id) {
        logger.info("deleting company from user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        user.setCompany(null);
        setRole(id, RoleEnum.WORKER);
        repository.save(user);
    }

    @CacheEvict(value = "fourHoursCache", key="'userWithId_' + #id")
    public void setRole(Long id, RoleEnum role) {
        logger.info("setting role for user with id: {}", id);
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

    public UserRequest getByEmail(String email) {
        logger.info("fetching user by email: {}", email);
        User user = repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("user with email " + email + "not found"));
        return UserRequest.fromEntityToDTO(user);
    }
}
