package github.axgiri.AuthenticationService.controller;

import java.util.List;
import java.util.stream.Collectors;

import github.axgiri.AuthenticationService.responses.CompanyResponse;
import github.axgiri.AuthenticationService.service.CompanyService;
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

import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.model.Company;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> get() {
        log.info("fetching all companies");
        List<Company> companies = service.get();
        List<CompanyResponse> response = companies.stream()
            .map(CompanyResponse::fromEntityToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@middleware.isCompanyMember(principal.username, #id)")
    @GetMapping("/{id}/full")
    public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
        log.info("fetching company with id: {}", id);
        Company company = service.getById(id);

        return ResponseEntity.ok(CompanyResponse.fromEntityToDTO(company));
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
