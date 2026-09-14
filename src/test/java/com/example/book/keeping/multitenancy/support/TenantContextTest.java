package com.example.book.keeping.multitenancy.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void shouldReturnDefaultTenantWhenNotSet() {
        assertEquals(TenantContext.DEFAULT_TENANT, TenantContext.getTenant());
    }

    @Test
    void shouldSetAndGetTenant() {
        TenantContext.setTenant("tenant_1");
        assertEquals("tenant_1", TenantContext.getTenant());
    }

    @Test
    void shouldClearTenant() {
        TenantContext.setTenant("tenant_1");
        TenantContext.clear();
        assertEquals(TenantContext.DEFAULT_TENANT, TenantContext.getTenant());
    }

    @Test
    void shouldDefaultToPublic() {
        assertEquals("public", TenantContext.DEFAULT_TENANT);
    }
}
