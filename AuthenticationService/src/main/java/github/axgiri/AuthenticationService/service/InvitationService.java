package github.axgiri.AuthenticationService.service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.model.Invitation;
import github.axgiri.AuthenticationService.repository.InvitationRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class InvitationService {

    private final InvitationRepository invitationRepository;

    public Invitation create(Company company, int validityDays) {
        log.info("creating invitation for company: {}", company);
        return new Invitation()
            .setCode(UUID.randomUUID().toString())
            .setCompany(company)
            .setCreatedAt(LocalDate.now())
            .setExpiresAt(LocalDate.now().plusDays(validityDays));
    }

    public Boolean validate(String code) {
        log.info("validating invitation code: {}", code);
        Invitation invitation = getByCode(code);
        ChronoLocalDate now = LocalDate.now();
        if (invitation.getExpiresAt().isAfter(now) || invitation.getExpiresAt().isEqual(now)) {
            return true;
        } else {
            return false;
        }
    }

    public Invitation getByCode(String code) {
        log.info("getting invitation by code: {}", code);
        return invitationRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("invalid or expired invitation code"));
    }

    public void delete(Invitation invitation) {
        invitationRepository.deleteById(invitation.getId());
    }
}

