package com.example.book.keeping.multitenancy.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantFilterTest {

    @InjectMocks
    private TenantFilter tenantFilter;

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void shouldSetTenantFromHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-ID", "tenant_1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<@Nullable String> capturedTenant = new AtomicReference<>();
        FilterChain chain = (_, _) -> capturedTenant.set(TenantContext.getTenant());

        tenantFilter.doFilterInternal(request, response, chain);

        assertEquals("tenant_1", capturedTenant.get());
    }

    @Test
    void shouldDefaultToPublicWhenNoHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<@Nullable String> capturedTenant = new AtomicReference<>();
        FilterChain chain = (_, _) -> capturedTenant.set(TenantContext.getTenant());

        tenantFilter.doFilterInternal(request, response, chain);

        assertEquals(TenantContext.DEFAULT_TENANT, capturedTenant.get());
    }

    @Test
    void shouldDefaultToPublicWhenHeaderBlank() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-ID", "  ");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<@Nullable String> capturedTenant = new AtomicReference<>();
        FilterChain chain = (_, _) -> capturedTenant.set(TenantContext.getTenant());

        tenantFilter.doFilterInternal(request, response, chain);

        assertEquals(TenantContext.DEFAULT_TENANT, capturedTenant.get());
    }

    @Test
    void shouldClearContextEvenWhenFilterThrows() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-ID", "tenant_1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doThrow(new ServletException("test")).when(chain).doFilter(request, response);

        try {
            tenantFilter.doFilterInternal(request, response, chain);
        } catch (ServletException e) {
            // expected
        }

        assertEquals(TenantContext.DEFAULT_TENANT, TenantContext.getTenant());
    }

    @Test
    void shouldDelegateToFilterChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        tenantFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
