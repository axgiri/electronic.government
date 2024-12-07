package github.axgiri.AuthenticationService.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.DTO.UserDTO;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Model.User;
import github.axgiri.AuthenticationService.Repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final CompanyService companyService;

    public UserService(UserRepository repository, CompanyService companyService) {
        this.repository = repository;
        this.companyService = companyService;
    }

    public List<UserDTO> getByCompanyId(Long id){
        logger.info("fetching users by company id: {}", id);
        return repository.findByCompanyId(id)
            .stream()
            .map(UserDTO::fromEntityToDTO)
            .collect(Collectors.toList());
    }

    public UserDTO getById(Long id){
        logger.info("fetching user by id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id: " + id + " not found"));
        return UserDTO.fromEntityToDTO(user);
    }

    public UserDTO add(UserDTO userDTO){
        logger.info("adding user: {}", userDTO);
        Company company = null;
        if(userDTO.getCompanyId() != null){
            company = companyService.getById(userDTO.getCompanyId()).toEntity();
        }
        User user = userDTO.toEntity(company);
        return UserDTO.fromEntityToDTO(repository.save(user));
    }

    public UserDTO update(UserDTO userDTO, Long id) {
        logger.info("updating user: {}", userDTO);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id: " + id + " not found"));
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setRole(userDTO.getRole());
        return UserDTO.fromEntityToDTO(repository.save(user));
    }    

    public void delete(Long id){
        logger.info("deleting user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        repository.delete(user);
    }

    public void deleteCompanyId(Long id){
        logger.info("deleting company from user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        user.setCompany(null);
        user.setRole(null);
        repository.save(user);
    }

    public void setRole(Long id, RoleEnum role){
        logger.info("setting role for user with id: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
        user.setRole(role);
        repository.save(user);
    }

    // TODO: public void validate(UserDTO userDTO){}
}
