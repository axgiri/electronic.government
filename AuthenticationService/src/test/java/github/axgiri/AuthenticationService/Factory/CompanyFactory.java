package github.axgiri.AuthenticationService.Factory;

import com.github.javafaker.Faker;

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.Model.Company;

public class CompanyFactory {

    private static final Faker faker = new Faker();

    public static Company create() {
        Company company = new Company();
        company.setId(faker.number().randomNumber());
        company.setName(faker.company().name());
        company.setPlan(null);
        company.setSubscriptionExpiration(null);
        company.setActive(false);
        return company;
    }

    public static CompanyDTO createDTO() {
        Company company = create();
        return CompanyDTO.fromEntityToDTO(company);
    }

    public static CompanyDTO createDTOFromEntity(Company company) {
        return CompanyDTO.fromEntityToDTO(company);
    }
}
