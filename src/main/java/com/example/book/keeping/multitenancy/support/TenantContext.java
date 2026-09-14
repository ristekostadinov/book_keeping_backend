package com.example.book.keeping.multitenancy.support;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Holds the current tenant identifier in a {@link ThreadLocal}.
 * <p>
 * The filter {@link TenantFilter} sets the tenant at the start of each HTTP request
 * and clears it afterward, so each thread (request) has its own tenant.
 * If no tenant is set, {@link #DEFAULT_TENANT} ({@value #DEFAULT_TENANT}) is returned.
 * </p>
 */
public final class TenantContext {

    private static final ThreadLocal<@Nullable String> CURRENT_TENANT = new ThreadLocal<>();

    /** Default tenant schema used when no {@code X-Tenant-ID} header is present. */
    public static final String DEFAULT_TENANT = "public";

    private TenantContext() {
    }

    /**
     * Sets the tenant identifier for the current thread.
     *
     * @param tenantId tenant schema name, or {@code null} to reset to default
     */
    public static void setTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Returns the tenant identifier for the current thread.
     *
     * @return current tenant schema name, or {@value #DEFAULT_TENANT} if none is set
     */
    public static String getTenant() {
        return Objects.requireNonNullElse(CURRENT_TENANT.get(),  DEFAULT_TENANT);
    }

    /**
     * Clears the tenant identifier for the current thread.
     * Should always be called in a {@code finally} block to prevent leaks.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
