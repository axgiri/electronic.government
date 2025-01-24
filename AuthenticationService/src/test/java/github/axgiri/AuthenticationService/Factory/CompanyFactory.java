package github.axgiri.AuthenticationService.Factory;

import com.github.javafaker.Faker;

import github.axgiri.AuthenticationService.requests.CompanyRequest;
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

    public static CompanyRequest createDTO() {
        Company company = create();
        return CompanyRequest.fromEntityToDTO(company);
    }

    public static CompanyRequest createDTOFromEntity(Company company) {
        return CompanyRequest.fromEntityToDTO(company);
    }
}
