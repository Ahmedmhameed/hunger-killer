package com.ahmedkh.order.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String getSubjectFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    public String getCustomerIdFromToken(String token) {
        Object id = getAllClaims(token).get("customerId");
        return id != null ? id.toString() : null;
    }

    public String getRoleFromToken(String token) {
        Object role = getAllClaims(token).get("role");
        return role != null ? role.toString() : "CUSTOMER";
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
    }
}
