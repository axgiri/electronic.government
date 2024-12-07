package github.axgiri.AuthenticationService.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import github.axgiri.AuthenticationService.Model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{
    
    @Query("SELECT c.active FROM Company c WHERE c.id = :id")
    Optional<Boolean> findActiveById(@Param("id") Long id);

    List<Company> findBySubscriptionExpiration(LocalDate date);
}
