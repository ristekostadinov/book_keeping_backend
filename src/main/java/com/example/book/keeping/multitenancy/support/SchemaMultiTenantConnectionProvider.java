package com.example.book.keeping.multitenancy.support;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Hibernate {@link MultiTenantConnectionProvider} that routes connections to the correct
 * PostgreSQL schema via {@link Connection#setSchema(String)}.
 * <p>
 * Uses a single {@link DataSource} (HikariCP pool). Each call to
 * {@link #getConnection(String)} obtains a connection and sets its schema.
 * On release the schema is reset to {@link TenantContext#DEFAULT_TENANT}.
 * Advertises {@link #handlesConnectionSchema()} as {@code true} so Hibernate does not
 * manage the schema itself.
 * </p>
 */
@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    /**
     * Creates the provider with the given pooled data source.
     *
     * @param dataSource HikariCP data source
     */
    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns a connection for bootstrap/database-metadata access without setting a schema.
     *
     * @return raw JDBC connection
     * @throws SQLException if a connection cannot be obtained
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Releases a connection obtained via {@link #getAnyConnection()}.
     *
     * @param connection connection to close
     * @throws SQLException if closing fails
     */
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Obtains a connection and sets its schema to the given tenant.
     *
     * @param tenantIdentifier tenant schema name
     * @return JDBC connection with schema set
     * @throws SQLException if the connection cannot be obtained or the schema cannot be set
     */
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setSchema(tenantIdentifier);
        return connection;
    }

    /**
     * Resets the schema to {@link TenantContext#DEFAULT_TENANT} and closes the connection.
     *
     * @param tenantIdentifier tenant whose connection is being released
     * @param connection       connection to reset and close
     * @throws SQLException if resetting or closing fails
     */
    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.setSchema(TenantContext.DEFAULT_TENANT);
        connection.close();
    }

    /**
     * Whether aggressive release after each statement is supported.
     *
     * @return {@code false}
     */
    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    /**
     * Whether this provider handles schema switching itself.
     *
     * @return {@code true} so Hibernate skips its own schema management
     */
    @Override
    public boolean handlesConnectionSchema() {
        return true;
    }

    @NullUnmarked
    @Override
    public boolean isUnwrappableAs(@NonNull Class<?> unwrapType) {
        return false;
    }

    @NullUnmarked
    @Override
    public <T> @NonNull T unwrap(@NonNull Class<T> unwrapType) {
        throw new UnsupportedOperationException("Cannot unwrap " + unwrapType);
    }
}
