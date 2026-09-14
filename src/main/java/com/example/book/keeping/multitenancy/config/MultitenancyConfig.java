package com.example.book.keeping.multitenancy.config;

import com.example.book.keeping.multitenancy.support.CurrentTenantResolver;
import com.example.book.keeping.multitenancy.support.SchemaMultiTenantConnectionProvider;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Registers schema-based multitenancy with Hibernate.
 * <p>
 * Implements {@link HibernatePropertiesCustomizer} to inject the
 * {@link SchemaMultiTenantConnectionProvider} and {@link CurrentTenantResolver}
 * as Hibernate properties before the {@code EntityManagerFactory} is built.
 * </p>
 */
@Configuration
public class MultitenancyConfig implements HibernatePropertiesCustomizer {

    private final SchemaMultiTenantConnectionProvider connectionProvider;
    private final CurrentTenantResolver tenantResolver;

    /**
     * Creates the config with required Hibernate multitenancy beans.
     *
     * @param connectionProvider tenant-aware connection provider
     * @param tenantResolver     resolver that reads {@code TenantContext}
     */
    public MultitenancyConfig(SchemaMultiTenantConnectionProvider connectionProvider,
                              CurrentTenantResolver tenantResolver) {
        this.connectionProvider = connectionProvider;
        this.tenantResolver = tenantResolver;
    }

    /**
     * Customizes Hibernate properties to enable schema-based multitenancy.
     *
     * @param hibernateProperties mutable map of Hibernate properties
     */
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        hibernateProperties.put("hibernate.tenant_identifier_resolver", tenantResolver);
        hibernateProperties.put("hibernate.physical_naming_strategy", PhysicalNamingStrategyStandardImpl.class.getName());
        hibernateProperties.put("hibernate.implicit_naming_strategy", ImplicitNamingStrategyJpaCompliantImpl.class.getName());
    }
}
