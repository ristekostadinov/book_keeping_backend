package com.example.book.keeping.tenant;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * Service for tenant CRUD and schema provisioning.
 * <p>
 * Creating a tenant inserts a row into {@code public.tenants}, creates a PostgreSQL
 * schema, and runs Flyway migrations from {@code classpath:db/migration/tenants}.
 * </p>
 */
@Slf4j
@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;

    /**
     * Creates the service.
     *
     * @param tenantRepository repository for {@code public.tenants}
     * @param dataSource       pooled data source for schema creation and Flyway
     */
    public TenantServiceImpl(TenantRepository tenantRepository, DataSource dataSource) {
        this.tenantRepository = tenantRepository;
        this.dataSource = dataSource;
    }

    /**
     * Creates a new tenant, provisioning its schema and running migrations.
     *
     * @param name display name (must be unique, used to derive schema name)
     * @return persisted tenant with generated id and schema name
     * @throws IllegalArgumentException if a tenant with the same name already exists
     * @throws RuntimeException         if schema creation or migration fails
     */
    @Transactional
    public Tenant createTenant(String name) {
        if (tenantRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tenant with name '" + name + "' already exists");
        }

        Tenant tenant = new Tenant(name, generateSchemaName(name));
        tenant = tenantRepository.save(tenant);

        provisionSchema(tenant.getSchemaName());

        log.info("Tenant created: id={}, name={}, schema={}", tenant.getId(), tenant.getName(), tenant.getSchemaName());
        return tenant;
    }

    /**
     * Lists all tenants.
     *
     * @return all tenants from {@code public.tenants}
     */
    @Transactional(readOnly = true)
    public List<Tenant> listTenants() {
        return tenantRepository.findAll();
    }

    /**
     * Finds a tenant by id.
     *
     * @param id primary key
     * @return matching tenant
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public Tenant getTenant(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + id));
    }

    /**
     * Finds a tenant by schema name.
     *
     * @param schemaName PostgreSQL schema name
     * @return matching tenant
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public Tenant getTenantBySchema(String schemaName) {
        return tenantRepository.findBySchemaName(schemaName)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found for schema: " + schemaName));
    }

    /**
     * Creates the PostgreSQL schema and runs Flyway migrations for the tenant.
     *
     * @param schemaName schema to provision
     * @throws RuntimeException if schema creation or migration fails
     */
    private void provisionSchema(String schemaName) {
        try (var connection = dataSource.getConnection()) {
            try (var stmt = connection.createStatement()) {
                stmt.execute("CREATE SCHEMA IF NOT EXISTS \"" + schemaName + "\"");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create schema: " + schemaName, e);
        }

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenants")
                    .table("flyway_schema_history")
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();
            log.info("Flyway migrations applied to schema: {}", schemaName);
        } catch (FlywayException e) {
            throw new RuntimeException("Failed to run Flyway migrations on schema: " + schemaName, e);
        }
    }

    /**
     * Derives a PostgreSQL schema name from a display name.
     * Lowercases, replaces non-alphanumeric runs with {@code _}, and prefixes with {@code tenant_}.
     *
     * @param tenantName display name
     * @return schema name (e.g. {@code tenant_acme_corp})
     */
    private String generateSchemaName(String tenantName) {
        String slug = tenantName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");
        return "tenant_" + slug;
    }
}
