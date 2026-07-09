package org.example.security;

import org.example.model.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(60, ChronoUnit.MINUTES))
                .claim("role", user.getRole().name())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }

    public Long extractUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Некорректные данные для JWT токена.");
        }

        return Long.valueOf(jwt.getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException exception) {
            return false;
        }
    }
}
