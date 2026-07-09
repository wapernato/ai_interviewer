package org.example.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey secretKey = createSecretKey();

        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = createSecretKey();

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private SecretKey createSecretKey() {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }
}