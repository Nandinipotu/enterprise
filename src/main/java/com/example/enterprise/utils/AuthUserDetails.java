package com.example.enterprise.utils;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.security.EnterPriseUserDetailsImpl;
import com.example.enterprise.security.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthUserDetails {

    @Value("${enterprise.app.jwtSecret}")
    private String secretKey;

     private SecretKey key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String getUserDetailsFromJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Claims claims = Jwts.parser()
                .verifyWith(key()) 
                .build()
                .parseSignedClaims(token) 
                .getPayload(); 
            return claims.getSubject();
        }

        return null;
    }

   public static String getUserId() {
        String userId;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        EnterPriseUserDetailsImpl userDetails = (EnterPriseUserDetailsImpl) authentication.getPrincipal();
        if (authentication.isAuthenticated()) {
            userId = userDetails.getUserId();
        } else {
            userId = "null";
        }
        return userId;
    }


}
