package github.axgiri.AuthenticationService.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import github.axgiri.AuthenticationService.Model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{
    Optional<Boolean> findActiveById(Long id);
}
