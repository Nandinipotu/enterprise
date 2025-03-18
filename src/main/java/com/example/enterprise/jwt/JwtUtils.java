package com.example.enterprise.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.entity.User;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtils {

    @Value("${enterprise.app.jwtSecret}")
    private String secretKey;

    @Value("${enterprise.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    private final Map<String, Date> invalidatedTokens = new HashMap<>();

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(jwtClaims(user))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);
            return !isTokenInvalidated(token);
        } catch (JwtException e) {
            return false;
        }
    }

    public Date getExpirationDateFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public String getUserEmailFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public void invalidateToken(String token) {
        Date expirationTime = getExpirationDateFromJwtToken(token);
        invalidatedTokens.put(token, expirationTime);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.containsKey(token) && invalidatedTokens.get(token).after(new Date());
    }

    private Map<String, Object> jwtClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", user.getUserName());
        claims.put("roles", user.getRoles());
        claims.put("permissions", user.getPermission());
        return claims;
    }

    public String generateEnterpriseUserToken(EnterPriseUser user) {
        return Jwts.builder()
                .subject(user.getUserEmail())
                .claims(enterpriseUserJwtClaimbs(user))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    private Map<String, Object> enterpriseUserJwtClaimbs(EnterPriseUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", user.getUserName());
        claims.put("role", user.getRole());
        claims.put("permissions", user.getPermissions());
        claims.put("orgId", user.getOrgId());
        return claims;
    }
}
