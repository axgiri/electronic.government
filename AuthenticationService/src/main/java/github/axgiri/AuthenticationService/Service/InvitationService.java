package github.axgiri.AuthenticationService.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.UUID;

import github.axgiri.AuthenticationService.Model.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.requests.CompanyRequest;
import github.axgiri.AuthenticationService.requests.InvitationRequest;
import github.axgiri.AuthenticationService.Model.Invitation;
import github.axgiri.AuthenticationService.Repository.InvitationRepository;

@Service
public class InvitationService {

    private static final Logger logger = LoggerFactory.getLogger(InvitationService.class);
    private final InvitationRepository invitationRepository;

    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public InvitationRequest create(Company company, int validityDays) {
        logger.info("creating invitation for company: {}", company);
        Invitation invitation = new Invitation()
            .setCode(UUID.randomUUID().toString())
            .setCompany(company)
            .setCreatedAt(LocalDate.now())
            .setExpiresAt(LocalDate.now().plusDays(validityDays));

        return InvitationRequest.fromEntityToDTO(invitationRepository.save(invitation));
    }

    public Boolean validate(String code) {
        logger.info("validating invitation code: {}", code);
        InvitationRequest invitationDTO = getByCode(code);
        ChronoLocalDate now = LocalDate.now();
        if (invitationDTO.getExpiresAt().isAfter(now) || invitationDTO.getExpiresAt().isEqual(now)) {
            return true;
        } else {
            return false;
        }
    }

    public InvitationRequest getByCode(String code) {
        logger.info("getting invitation by code: {}", code);
        Invitation invitation = invitationRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("invalid or expired invitation code"));
        return InvitationRequest.fromEntityToDTO(invitation);
    }

    public void delete(InvitationRequest invitationDTO) {
        invitationRepository.deleteById(invitationDTO.getId());
    }
}

