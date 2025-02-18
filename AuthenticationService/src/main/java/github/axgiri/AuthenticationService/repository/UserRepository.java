package github.axgiri.AuthenticationService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import github.axgiri.AuthenticationService.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    List<User> findByCompanyId(Long id);
    
    Optional<User> findByEmail(String email);
}
