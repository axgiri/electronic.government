package github.axgiri.AuthenticationService.Command;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Repository.CompanyRepository;

@Service
public class CompanyExpirationCommand {
    private final CompanyRepository repository;

    public CompanyExpirationCommand(CompanyRepository repository) {
        this.repository = repository;
    }

    @Transactional
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
