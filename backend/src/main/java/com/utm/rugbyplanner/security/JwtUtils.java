package com.utm.rugbyplanner.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT utility — generates, validates, and parses JWT Bearer tokens.
 *
 * Used by:
 *   AuthService          → generateToken() after successful login
 *   JwtAuthenticationFilter → validateToken() + getUsernameFromToken()
 *                             on every incoming HTTP request
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // ── Signing key ───────────────────────────────────────────────────────────

    private Key getSigningKey() {
        // jwtSecret must be ≥ 32 ASCII characters (256 bits) for HS256
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ── Token generation ──────────────────────────────────────────────────────

    /**
     * Generate a signed JWT for the authenticated user.
     * Called in AuthService after Spring Security verifies credentials.
     */
    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Token parsing ─────────────────────────────────────────────────────────

    /**
     * Extract the username (subject) from a valid JWT.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ── Token validation ──────────────────────────────────────────────────────

    /**
     * Return true if the token is well-formed, signed correctly, and not expired.
     * Any failure is caught and logged; the method returns false rather than
     * propagating the exception (the filter handles it gracefully).
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string empty: {}", e.getMessage());
        }
        return false;
    }
}
