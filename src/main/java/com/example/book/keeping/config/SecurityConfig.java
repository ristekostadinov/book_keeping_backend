package com.example.book.keeping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the REST API.
 * <p>
 * Disables CSRF (no browser sessions) and permits all requests. Intended for
 * development; replace with proper authentication before production use.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Builds the security filter chain.
     *
     * @param http Spring Security HTTP builder
     * @return configured filter chain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();
    }
}
