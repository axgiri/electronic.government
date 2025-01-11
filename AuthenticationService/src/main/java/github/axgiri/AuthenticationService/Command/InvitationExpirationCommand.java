package github.axgiri.AuthenticationService.Command;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.axgiri.AuthenticationService.Model.Invitation;
import github.axgiri.AuthenticationService.Repository.InvitationRepository;

@Service
public class InvitationExpirationCommand {
    private final InvitationRepository repository;

    @Autowired
    public InvitationExpirationCommand(InvitationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteTokens() {
        LocalDate now = LocalDate.now();

        List<Invitation> active = repository.findByExpiresAt(now);

        for (Invitation invitation : active) {
            repository.delete(invitation);
        }
    }
}
