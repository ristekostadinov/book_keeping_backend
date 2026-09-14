package com.example.book.keeping.multitenancy.support;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Hibernate {@link CurrentTenantIdentifierResolver} that delegates to {@link TenantContext}.
 * <p>
 * Registered via {@link com.example.book.keeping.multitenancy.config.MultitenancyConfig}
 * as {@code hibernate.tenant_identifier_resolver}. Hibernate calls
 * {@link #resolveCurrentTenantIdentifier()} when opening a session to determine
 * which schema to use.
 * </p>
 */
@Component
public class CurrentTenantResolver implements CurrentTenantIdentifierResolver<String> {

    /**
     * Resolves the current tenant identifier from {@link TenantContext}.
     *
     * @return current tenant schema name, never {@code null}
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getTenant();
    }

    /**
     * Whether to validate that an existing session's tenant matches the resolved one.
     *
     * @return {@code false}; no cross-session validation
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    /**
     * Checks if the given tenant is the root tenant with access to shared data.
     *
     * @param tenantId tenant identifier to check
     * @return {@code true} if {@code tenantId} equals {@link TenantContext#DEFAULT_TENANT}
     */
    @Override
    public boolean isRoot(String tenantId) {
        return TenantContext.DEFAULT_TENANT.equals(tenantId);
    }
}
