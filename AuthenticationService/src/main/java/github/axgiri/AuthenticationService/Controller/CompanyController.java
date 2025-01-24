package github.axgiri.AuthenticationService.Controller;

import java.util.List;

import github.axgiri.AuthenticationService.Model.Company;
import github.axgiri.AuthenticationService.responses.CompanyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.axgiri.AuthenticationService.requests.CompanyRequest;
import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.Service.CompanyService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;

    @GetMapping
    public ResponseEntity<List<CompanyRequest>> get() {
        log.info("fetching all companies");
        List<CompanyRequest> companies = service.get();
        return ResponseEntity.ok(companies);
    }

    @PreAuthorize("@middleware.isCompanyMember(principal.username, #id)")
    @GetMapping("/{id}/full")
    public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
        log.info("fetching company with id: {}", id);
        Company company = service.getById(id);

        return ResponseEntity.ok(new CompanyResponse(company));
    }

    @PreAuthorize("@middleware.isCompanyAdmin(principal.username, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("deleting company with id: {}", id);
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@middleware.isCompanyMember(principal.username, #id)")
    @GetMapping("/subscriptions/isActive/{id}")
    public ResponseEntity<Boolean> isActive(@PathVariable Long id) {
        log.info("is avtive: {}", id);
        return ResponseEntity.ok(service.isActive(id));
    }

    @PreAuthorize("@middleware.isCompanyAdmin(principal.username, #id)")
    @PostMapping("/subscriptions/buy/{id}")
    public ResponseEntity<Void> buy(@PathVariable Long id, @RequestParam PlanEnum plan) {
        log.info("buying plan for: {}", id);
        service.buy(id, plan);
        return ResponseEntity.ok().build();
    }
}
