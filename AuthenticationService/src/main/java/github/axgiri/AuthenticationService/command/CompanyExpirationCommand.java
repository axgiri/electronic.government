package github.axgiri.AuthenticationService.command;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.model.Company;
import github.axgiri.AuthenticationService.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CompanyExpirationCommand {
    private final CompanyRepository repository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void closeCompanySubscription() {
        LocalDate now = LocalDate.now();
        List<Company> active = repository.findBySubscriptionExpiration(now);

        for (Company company : active) {
            company.setActive(false);
            company.setPlan(null);
            repository.save(company);
        }
    }
}
