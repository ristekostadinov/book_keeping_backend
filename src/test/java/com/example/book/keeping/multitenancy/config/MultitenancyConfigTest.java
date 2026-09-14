package com.example.book.keeping.multitenancy.config;

import com.example.book.keeping.multitenancy.support.CurrentTenantResolver;
import com.example.book.keeping.multitenancy.support.SchemaMultiTenantConnectionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MultitenancyConfigTest {

    @Mock
    private SchemaMultiTenantConnectionProvider connectionProvider;

    @Mock
    private CurrentTenantResolver tenantResolver;

    @InjectMocks
    private MultitenancyConfig config;

    @Test
    void shouldSetHibernateMultitenancyProperties() {
        Map<String, Object> props = new HashMap<>();

        config.customize(props);

        assertEquals(connectionProvider, props.get("hibernate.multi_tenant_connection_provider"));
        assertEquals(tenantResolver, props.get("hibernate.tenant_identifier_resolver"));
        assertNotNull(props.get("hibernate.physical_naming_strategy"));
        assertNotNull(props.get("hibernate.implicit_naming_strategy"));
    }
}
