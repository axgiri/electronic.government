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

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.Factory.CompanyFactory;
import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.Repository.CompanyRepository;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService service;
 
    @Test
    public void buyMonthlyPlanTest(){
        Company company = CompanyFactory.create();
        PlanEnum plan = PlanEnum.MONTHLY;
        when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        service.buy(company.getId(), plan);
        assertEquals(plan, company.getPlan());
        assertTrue(company.isActive());
        assertEquals(LocalDate.now().plusMonths(1), company.getSubscriptionExpiration());
        verify(companyRepository, times(1)).findById(company.getId());
        verify(companyRepository, times(1)).save(company);
    }

    @Test
    public void buyYearlyPlanTest(){
        Company company = CompanyFactory.create();
        PlanEnum plan = PlanEnum.YEARLY;        
        when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        service.buy(company.getId(), plan);
        assertEquals(plan, company.getPlan());
        assertTrue(company.isActive());
        assertEquals(LocalDate.now().plusYears(1), company.getSubscriptionExpiration());
        verify(companyRepository, times(1)).findById(company.getId());
        verify(companyRepository, times(1)).save(company);
    }

    @Test
    public void buyCompanyNotFoundTest(){
        Long companyId = 1L;
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

