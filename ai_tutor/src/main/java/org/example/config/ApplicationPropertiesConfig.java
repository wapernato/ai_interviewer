package org.example.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        InternalApiProperties.class,
        BootstrapAdminProperties.class,
        AppBackendUrlProperty.class
})
public class ApplicationPropertiesConfig {
}