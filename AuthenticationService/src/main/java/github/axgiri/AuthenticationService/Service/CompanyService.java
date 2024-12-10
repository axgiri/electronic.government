package github.axgiri.AuthenticationService.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Repository.CompanyRepository;

@Service
public class CompanyService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private final CompanyRepository repository;

    @Autowired
    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public List<CompanyDTO> get(){
        logger.info("fetching all companies");
        List<Company> companies = repository.findAll();
        return companies.stream()
            .map(CompanyDTO::fromEntityToDTO)
            .collect(Collectors.toList());
    }

    public CompanyDTO getById(Long id){
        logger.info("fetching company with id: {}", id );
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("comopany with id: " + id + " not found"));
        return CompanyDTO.fromEntityToDTO(company);
    }

    public CompanyDTO add(CompanyDTO companyDTO){
        logger.info("creating company with data: {}", companyDTO);
        Company company = companyDTO.toEntity();
        repository.save(company);
        return CompanyDTO.fromEntityToDTO(company);
    }

    public void delete(Long id){
        logger.info("deleting company with id: {}", id);
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("company with id " + id + " not found"));
        repository.delete(company);
    }

    public boolean isActive(Long id){
        logger.info("is active company with id: {}", id);
        return repository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("company with id " + id + " not found"));
    }

    public void buy(Long id, PlanEnum plan){
        logger.info("buying plan for company with id: {}", id);
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("company with id " + id + " not found"));

        if (plan == PlanEnum.MONTHLY){
            company.setSubscriptionExpiration(LocalDate.now().plusMonths(1));
        } else if (plan == PlanEnum.YEARLY){
            company.setSubscriptionExpiration(LocalDate.now().plusYears(1));
        }
        
        company.setPlan(plan);
        company.setActive(true);
        repository.save(company);
    }
}
