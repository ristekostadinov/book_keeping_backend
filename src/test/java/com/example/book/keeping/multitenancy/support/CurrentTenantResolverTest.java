package com.example.book.keeping.multitenancy.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrentTenantResolverTest {

    private final CurrentTenantResolver resolver = new CurrentTenantResolver();

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void shouldResolveDefaultTenant() {
        assertEquals(TenantContext.DEFAULT_TENANT, resolver.resolveCurrentTenantIdentifier());
    }

    @Test
    void shouldResolveConfiguredTenant() {
        TenantContext.setTenant("tenant_42");
        assertEquals("tenant_42", resolver.resolveCurrentTenantIdentifier());
    }

    @Test
    void shouldNotValidateExistingSessions() {
        assertFalse(resolver.validateExistingCurrentSessions());
    }

    @Test
    void shouldIdentifyRootTenant() {
        assertTrue(resolver.isRoot("public"));
    }

    @Test
    void shouldNotIdentifyNonRootTenant() {
        assertFalse(resolver.isRoot("tenant_1"));
    }
}
