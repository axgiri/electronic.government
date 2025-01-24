package github.axgiri.AuthenticationService.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.requests.CompanyRequest;
import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Repository.CompanyRepository;

@Slf4j
@Service
public class CompanyService {
    
    private final CompanyRepository repository;

    @Autowired
    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public List<CompanyRequest> get() {
        log.info("fetching all companies");
        List<Company> companies = repository.findAll();
        return companies.stream()
            .map(CompanyRequest::fromEntityToDTO)
            .collect(Collectors.toList());
    }

    public Company getById(Long id) {
        log.info("fetching company with id: {}", id );
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("comopany with id: " + id + " not found"));
    }

    public CompanyRequest add(CompanyRequest companyDTO) {
        log.info("creating company with data: {}", companyDTO);
        Company company = companyDTO.toEntity();
        repository.save(company);
        return CompanyRequest.fromEntityToDTO(company);
    }

    public void delete(Long id) {
        log.info("deleting company with id: {}", id);
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("company with id " + id + " not found"));
        repository.delete(company);
    }

    @Cacheable(value = "oneHourCache", key = "'isCompany_' + #id + '_Active'")
    public boolean isActive(Long id) {
        log.info("is active company with id: {}", id);
        return repository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("company with id " + id + " not found"));
    }

    @Caching(evict = {
            @CacheEvict(value = "oneHourCache", allEntries = true),
            @CacheEvict(value = "isCompanyAndTokenValid", allEntries = true)
    })
    public void buy(Long id, PlanEnum plan) {
        log.info("buying plan for company with id: {}", id);
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("company with id " + id + " not found"));

        if (plan == PlanEnum.MONTHLY) {
            company.setSubscriptionExpiration(LocalDate.now().plusMonths(1));
        } else if (plan == PlanEnum.YEARLY) {
            company.setSubscriptionExpiration(LocalDate.now().plusYears(1));
        }
        
        company.setPlan(plan);
        company.setActive(true);
        repository.save(company);
    }
}
