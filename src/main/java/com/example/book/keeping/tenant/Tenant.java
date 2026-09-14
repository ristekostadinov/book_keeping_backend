package com.example.book.keeping.tenant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * JPA entity mapped to {@code public.tenants}.
 * <p>
 * Stores the tenant registry. Business data lives in per-tenant schemas
 * (e.g. {@code tenant_acme_corp}).
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "tenants", schema = "public")
public class Tenant {

    /** Primary key, database-generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Display name of the tenant. */
    @Column(nullable = false)
    private String name;

    /** PostgreSQL schema name for this tenant (e.g. {@code tenant_acme_corp}). */
    @Column(name = "schema_name", nullable = false, unique = true)
    private String schemaName;

    /** Whether the tenant is active. */
    @Column(nullable = false)
    private Boolean active = true;

    /** Creation timestamp, set once on persist. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /** Sets {@link #createdAt} to now before insert. */
    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    /** Required by JPA. */
    public Tenant() {
    }

    /**
     * Creates a tenant with name and schema.
     *
     * @param name       display name
     * @param schemaName PostgreSQL schema name
     */
    public Tenant(String name, String schemaName) {
        this.name = name;
        this.schemaName = schemaName;
        this.active = true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
