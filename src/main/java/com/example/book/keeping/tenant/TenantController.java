package com.example.book.keeping.tenant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for tenant management.
 * <p>
 * All endpoints are under {@code /api/tenants}. CSRF is disabled and all requests
 * are permitted via {@link com.example.book.keeping.config.SecurityConfig}.
 * </p>
 */
@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    /**
     * Creates the controller.
     *
     * @param tenantServiceImpl tenant service
     */
    public TenantController(TenantServiceImpl tenantServiceImpl) {
        this.tenantService = tenantServiceImpl;
    }

    /**
     * Creates a new tenant.
     *
     * @param request JSON body with {@code name} field
     * @return {@code 201 Created} with tenant, or {@code 400} on validation error
     */
    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tenant name is required"));
        }
        try {
            Tenant tenant = tenantService.createTenant(name);
            return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lists all tenants.
     *
     * @return {@code 200 OK} with tenant list
     */
    @GetMapping
    public ResponseEntity<List<Tenant>> listTenants() {
        return ResponseEntity.ok(tenantService.listTenants());
    }

    /**
     * Gets a single tenant by id.
     *
     * @param id tenant primary key
     * @return {@code 200 OK} with tenant, or {@code 404} if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTenant(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(tenantService.getTenant(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
