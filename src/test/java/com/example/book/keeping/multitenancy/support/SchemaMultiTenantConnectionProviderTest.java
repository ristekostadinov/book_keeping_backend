package com.example.book.keeping.multitenancy.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchemaMultiTenantConnectionProviderTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    private SchemaMultiTenantConnectionProvider provider;

    @BeforeEach
    void setup() throws Exception {
        provider = new SchemaMultiTenantConnectionProvider(dataSource);
        lenient().when(dataSource.getConnection()).thenReturn(connection);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void shouldGetAnyConnection() throws Exception {
        Connection result = provider.getAnyConnection();
        assertEquals(connection, result);
        verify(dataSource).getConnection();
    }

    @Test
    void shouldReleaseAnyConnection() throws Exception {
        provider.releaseAnyConnection(connection);
        verify(connection).close();
    }

    @Test
    void shouldGetConnectionWithSchemaSet() throws Exception {
        Connection result = provider.getConnection("tenant_1");

        assertEquals(connection, result);
        verify(connection).setSchema("tenant_1");
    }

    @Test
    void shouldReleaseConnectionAndResetSchema() throws Exception {
        provider.releaseConnection("tenant_1", connection);

        verify(connection).setSchema("public");
        verify(connection).close();
    }

    @Test
    void shouldNotSupportAggressiveRelease() {
        assertFalse(provider.supportsAggressiveRelease());
    }

    @Test
    void shouldHandleConnectionSchema() {
        assertTrue(provider.handlesConnectionSchema());
    }

    @Test
    void shouldNotBeUnwrappable() {
        assertFalse(provider.isUnwrappableAs(Object.class));
    }

    @Test
    void shouldThrowOnUnwrap() {
        assertThrows(UnsupportedOperationException.class, () -> provider.unwrap(Object.class));
    }
}
