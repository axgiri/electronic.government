package github.axgiri.AuthenticationService.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.DTO.InvitationDTO;
import github.axgiri.AuthenticationService.Model.Invitation;
import github.axgiri.AuthenticationService.Repository.InvitationRepository;

@Service
public class InvitationService {

    private static final Logger logger = LoggerFactory.getLogger(InvitationService.class);
    private final InvitationRepository invitationRepository;

    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public InvitationDTO create(CompanyDTO companyDTO, int validityDays) {
        logger.info("creating invitation for company: {}", companyDTO);
        Invitation invitation = new Invitation();
        invitation.setCode(UUID.randomUUID().toString());
        invitation.setCompany(companyDTO.toEntity());
        invitation.setCreatedAt(LocalDate.now());
        invitation.setExpiresAt(LocalDate.now().plusDays(validityDays));
        return InvitationDTO.fromEntityToDTO(invitationRepository.save(invitation));
    }

    public Boolean validate(String code) {
        logger.info("validating invitation code: {}", code);
        InvitationDTO invitationDTO = getByCode(code);
        ChronoLocalDate now = LocalDate.now();
        if (invitationDTO.getExpiresAt().isAfter(now) || invitationDTO.getExpiresAt().isEqual(now)) {
            return true;
        } else {
            return false;
        }
    }

    public InvitationDTO getByCode(String code){
        logger.info("getting invitation by code: {}", code);
        Invitation invitation = invitationRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("invalid or expired invitation code"));
        return InvitationDTO.fromEntityToDTO(invitation);
    }

    public void delete(InvitationDTO invitationDTO) {
        invitationRepository.deleteById(invitationDTO.getId());
    }
}

