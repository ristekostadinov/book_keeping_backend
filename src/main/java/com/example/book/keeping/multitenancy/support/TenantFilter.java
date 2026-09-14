package com.example.book.keeping.multitenancy.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that resolves the current tenant from the {@value #TENANT_HEADER}
 * HTTP header and stores it in {@link TenantContext}.
 * <p>
 * Runs at {@link Ordered#HIGHEST_PRECEDENCE} so it executes before any other filter.
 * If the header is missing or blank, defaults to {@link TenantContext#DEFAULT_TENANT}.
 * The thread-local is always cleared in a {@code finally} block.
 * </p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    /** HTTP header that carries the tenant schema name. */
    private static final String TENANT_HEADER = "X-Tenant-ID";

    /**
     * Extracts the tenant identifier from the request header, binds it to
     * {@link TenantContext}, delegates to the filter chain, and clears the context.
     *
     * @param request     incoming HTTP request
     * @param response    outgoing HTTP response
     * @param filterChain remaining filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tenantId = request.getHeader(TENANT_HEADER);
        try {
            TenantContext.setTenant(
                    tenantId != null && !tenantId.isBlank() ? tenantId : TenantContext.DEFAULT_TENANT
            );
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
