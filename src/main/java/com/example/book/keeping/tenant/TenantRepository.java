package com.example.book.keeping.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Tenant} entities in the {@code public} schema.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Finds a tenant by its PostgreSQL schema name.
     *
     * @param schemaName schema name (e.g. {@code tenant_acme_corp})
     * @return tenant if found
     */
    Optional<Tenant> findBySchemaName(String schemaName);

    /**
     * Checks whether a tenant with the given display name exists.
     *
     * @param name display name to check
     * @return {@code true} if a tenant with that name exists
     */
    boolean existsByName(String name);
}
