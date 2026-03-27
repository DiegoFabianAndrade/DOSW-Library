package edu.eci.dosw.tdd.security;

import edu.eci.dosw.tdd.core.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long ttlMillis;

    public JwtService(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.ttl-ms}") long ttlMillis) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.ttlMillis = ttlMillis;
    }

    public String generateToken(Integer userId, String username, Role role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .claims(Map.of("userId", userId, "role", role.name()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + ttlMillis))
                .signWith(key)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public AppUserPrincipal getPrincipal(String token) {
        Claims claims = parseClaims(token);
        return new AppUserPrincipal(
                claims.get("userId", Integer.class),
                claims.getSubject(),
                Role.valueOf(claims.get("role", String.class)));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
