package github.axgiri.AuthenticationService.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import github.axgiri.AuthenticationService.Model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    List<User> findByCompanyId(Long id);    
}
