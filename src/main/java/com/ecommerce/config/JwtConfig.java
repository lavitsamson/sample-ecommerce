
package com.ecommerce.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtConfig {
    private final SecretKey key;
    private static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 10; // 10 hours

    public JwtConfig(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + TOKEN_VALIDITY);

        return Jwts.builder()
                .subject(email)      // instead of setSubject()
                .issuedAt(now)      // instead of setIssuedAt()
                .expiration(validity)// instead of setExpiration()
                .id(UUID.randomUUID().toString()) // instead of setId()
                .signWith(key)       // this is still current
                .compact();

    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}