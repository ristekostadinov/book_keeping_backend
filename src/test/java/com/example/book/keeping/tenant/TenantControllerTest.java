package com.example.book.keeping.tenant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TenantControllerTest {

    @Mock
    private TenantServiceImpl tenantService;

    @InjectMocks
    private TenantController tenantController;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(tenantController).build();
    }

    @Test
    void shouldCreateTenant() throws Exception {
        Tenant tenant = new Tenant("Acme Corp", "tenant_acme_corp");
        tenant.setId(1L);
        when(tenantService.createTenant("Acme Corp")).thenReturn(tenant);

        mockMvc().perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Acme Corp\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Acme Corp"))
                .andExpect(jsonPath("$.schemaName").value("tenant_acme_corp"));
    }

    @Test
    void shouldRejectBlankName() throws Exception {
        mockMvc().perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tenant name is required"));
    }

    @Test
    void shouldRejectMissingName() throws Exception {
        mockMvc().perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tenant name is required"));
    }

    @Test
    void shouldRejectDuplicateName() throws Exception {
        when(tenantService.createTenant("Acme Corp"))
                .thenThrow(new IllegalArgumentException("Tenant with name 'Acme Corp' already exists"));

        mockMvc().perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Acme Corp\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tenant with name 'Acme Corp' already exists"));
    }

    @Test
    void shouldListTenants() throws Exception {
        Tenant t1 = new Tenant("Acme", "tenant_acme");
        t1.setId(1L);
        Tenant t2 = new Tenant("Globex", "tenant_globex");
        t2.setId(2L);
        when(tenantService.listTenants()).thenReturn(List.of(t1, t2));

        mockMvc().perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Acme"))
                .andExpect(jsonPath("$[1].name").value("Globex"));
    }

    @Test
    void shouldGetTenantById() throws Exception {
        Tenant tenant = new Tenant("Acme", "tenant_acme");
        tenant.setId(1L);
        when(tenantService.getTenant(1L)).thenReturn(tenant);

        mockMvc().perform(get("/api/tenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme"));
    }

    @Test
    void shouldReturn404WhenTenantNotFound() throws Exception {
        when(tenantService.getTenant(999L))
                .thenThrow(new IllegalArgumentException("Tenant not found: 999"));

        mockMvc().perform(get("/api/tenants/999"))
                .andExpect(status().isNotFound());
    }
}
