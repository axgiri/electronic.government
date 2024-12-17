package github.axgiri.AuthenticationService.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Repository.CompanyRepository;
import com.github.javafaker.Faker;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService service;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
    }

    @Test
    public void buyMonthlyPlanTest(){
        Long companyId = faker.number().randomNumber();
        PlanEnum plan = PlanEnum.MONTHLY;
        Company company = new Company();
        company.setId(companyId);
        company.setPlan(null);
        company.setSubscriptionExpiration(null);
        company.setActive(false);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        service.buy(companyId, plan);
        assertEquals(plan, company.getPlan());
        assertTrue(company.isActive());
        assertEquals(LocalDate.now().plusMonths(1), company.getSubscriptionExpiration());
        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, times(1)).save(company);
    }

    @Test
    public void buyYearlyPlanTest(){
        Long companyId = faker.number().randomNumber();
        PlanEnum plan = PlanEnum.YEARLY;
        Company company = new Company();
        company.setId(companyId);
        company.setPlan(null);
        company.setSubscriptionExpiration(null);
        company.setActive(false);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        service.buy(companyId, plan);
        assertEquals(plan, company.getPlan());
        assertTrue(company.isActive());
        assertEquals(LocalDate.now().plusYears(1), company.getSubscriptionExpiration());
        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, times(1)).save(company);
    }

    @Test
    public void buyCompanyNotFoundTest(){
        Long companyId = faker.number().randomNumber();
        PlanEnum plan = PlanEnum.MONTHLY;
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.buy(companyId, plan);
        });

        assertEquals("company with id " + companyId + " not found", exception.getMessage());
        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, never()).save(any(Company.class));
    }
}

