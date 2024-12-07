package github.axgiri.AuthenticationService.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import github.axgiri.AuthenticationService.Model.Invitation;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByCode(String code);

    List<Invitation> findByExpiresAt(LocalDate date);
}
