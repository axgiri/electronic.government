package github.axgiri.AuthenticationService.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.axgiri.AuthenticationService.DTO.UserDTO;
import github.axgiri.AuthenticationService.Service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/getByCompanyId/{id}")
    public ResponseEntity<List<UserDTO>> getByCompanyId(@PathVariable Long id){
        logger.info("fetching all user with company id: {}", id);
        return ResponseEntity.ok(service.getByCompanyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id){
        logger.info("fetching user by id: {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userdDTO){
        logger.info("creating user with data: {}", userdDTO);
        return ResponseEntity.ok(service.add(userdDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> update(@RequestBody UserDTO userDTO, @PathVariable Long id){
        logger.info("updating user: {}", userDTO);
        return ResponseEntity.ok(service.update(userDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        logger.info("deleting user with id: {}", id);
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> outFromCompany(@PathVariable Long id){
        logger.info("deleting company from user with id: {}", id);
        service.deleteCompanyId(id);
        return ResponseEntity.ok().build();
    }
}
