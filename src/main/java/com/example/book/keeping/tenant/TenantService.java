package com.example.book.keeping.tenant;

import java.util.List;

public interface TenantService {
    Tenant createTenant(String name);
    List<Tenant> listTenants();
    Tenant getTenant(Long id);
    Tenant getTenantBySchema(String schemaName);
}
