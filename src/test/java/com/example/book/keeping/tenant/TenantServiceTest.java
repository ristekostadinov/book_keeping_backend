package com.example.book.keeping.tenant;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @InjectMocks
    private TenantServiceImpl tenantService;

    @Test
    void shouldCreateTenant() throws Exception {
        when(tenantRepository.existsByName("Acme Corp")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> {
            Tenant t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenReturn(true);

        try (MockedStatic<Flyway> flywayStatic = mockStatic(Flyway.class)) {
            FluentConfiguration config = mock(FluentConfiguration.class);
            Flyway flyway = mock(Flyway.class);
            flywayStatic.when(Flyway::configure).thenReturn(config);
            when(config.dataSource(any(DataSource.class))).thenReturn(config);
            when(config.schemas(anyString())).thenReturn(config);
            when(config.locations(any(String[].class))).thenReturn(config);
            when(config.table(anyString())).thenReturn(config);
            when(config.baselineOnMigrate(anyBoolean())).thenReturn(config);
            when(config.load()).thenReturn(flyway);
            when(flyway.migrate()).thenReturn(null);

            Tenant result = tenantService.createTenant("Acme Corp");

            assertNotNull(result);
            assertEquals("Acme Corp", result.getName());
            assertEquals("tenant_acme_corp", result.getSchemaName());
            assertTrue(result.getActive());
            verify(tenantRepository).save(any(Tenant.class));
            verify(statement).execute("CREATE SCHEMA IF NOT EXISTS \"tenant_acme_corp\"");
        }
    }

    @Test
    void shouldRejectDuplicateTenantName() {
        when(tenantRepository.existsByName("Acme Corp")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tenantService.createTenant("Acme Corp")
        );
        assertEquals("Tenant with name 'Acme Corp' already exists", ex.getMessage());
    }

    @Test
    void shouldListTenants() {
        Tenant t1 = new Tenant("Acme", "tenant_acme");
        Tenant t2 = new Tenant("Globex", "tenant_globex");
        when(tenantRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Tenant> result = tenantService.listTenants();

        assertEquals(2, result.size());
        verify(tenantRepository).findAll();
    }

    @Test
    void shouldGetTenantById() {
        Tenant t = new Tenant("Acme", "tenant_acme");
        t.setId(1L);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(t));

        Tenant result = tenantService.getTenant(1L);

        assertEquals("Acme", result.getName());
    }

    @Test
    void shouldThrowWhenTenantNotFound() {
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tenantService.getTenant(999L));
    }

    @Test
    void shouldGenerateSchemaNameFromTenantName() throws Exception {
        when(tenantRepository.existsByName("My Cool Company!")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> {
            Tenant t = invocation.getArgument(0);
            t.setId(2L);
            return t;
        });
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenReturn(true);

        try (MockedStatic<Flyway> flywayStatic = mockStatic(Flyway.class)) {
            FluentConfiguration config = mock(FluentConfiguration.class);
            Flyway flyway = mock(Flyway.class);
            flywayStatic.when(Flyway::configure).thenReturn(config);
            when(config.dataSource(any(DataSource.class))).thenReturn(config);
            when(config.schemas(anyString())).thenReturn(config);
            when(config.locations(any(String[].class))).thenReturn(config);
            when(config.table(anyString())).thenReturn(config);
            when(config.baselineOnMigrate(anyBoolean())).thenReturn(config);
            when(config.load()).thenReturn(flyway);
            when(flyway.migrate()).thenReturn(null);

            Tenant result = tenantService.createTenant("My Cool Company!");

            assertEquals("tenant_my_cool_company", result.getSchemaName());
        }
    }
}
