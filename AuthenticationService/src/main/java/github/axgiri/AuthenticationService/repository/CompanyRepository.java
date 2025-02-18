package github.axgiri.AuthenticationService.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import github.axgiri.AuthenticationService.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{
    Optional<Boolean> findActiveById(Long id);

    List<Company> findBySubscriptionExpiration(LocalDate date);
}
