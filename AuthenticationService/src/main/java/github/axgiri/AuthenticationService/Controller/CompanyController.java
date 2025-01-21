package github.axgiri.AuthenticationService.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.axgiri.AuthenticationService.DTO.CompanyDTO;
import github.axgiri.AuthenticationService.Enum.PlanEnum;
import github.axgiri.AuthenticationService.Service.CompanyService;

@RestController
@RequestMapping("/api/auth/companies")
public class CompanyController {
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CompanyDTO>> get() {
        logger.info("fetching all companies");
        List<CompanyDTO> companies = service.get();
        return ResponseEntity.ok(companies);
    }

    @PreAuthorize("@middleware.isCompanyMember(principal.username, #id)")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        logger.info("fetching company with id: {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("@middleware.isCompanyAdmin(principal.username, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("deleting company with id: {}", id);
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@middleware.isCompanyMember(principal.username, #id)")
    @GetMapping("/subscriptions/isActive/{id}")
    public ResponseEntity<Boolean> isActive(@PathVariable Long id) {
        logger.info("is avtive: {}", id);
        return ResponseEntity.ok(service.isActive(id));
    }

    @PreAuthorize("@middleware.isCompanyAdmin(principal.username, #id)")
    @PostMapping("/subscriptions/buy/{id}")
    public ResponseEntity<Void> buy(@PathVariable Long id, @RequestParam PlanEnum plan) {
        logger.info("buying plan for: {}", id);
        service.buy(id, plan);
        return ResponseEntity.ok().build();
    }
}
