package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "bootstrap.admin")
public record BootstrapAdminProperties (

    boolean enabled,
    String username,
    String email,
    String password
) {}
