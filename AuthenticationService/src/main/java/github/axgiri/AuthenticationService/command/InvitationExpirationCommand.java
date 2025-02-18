package github.axgiri.AuthenticationService.command;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.model.Invitation;
import github.axgiri.AuthenticationService.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvitationExpirationCommand {

    private final InvitationRepository repository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteTokens() {
        LocalDate now = LocalDate.now();

        List<Invitation> active = repository.findByExpiresAt(now);

        for (Invitation invitation : active) {
            repository.delete(invitation);
        }
    }
}
