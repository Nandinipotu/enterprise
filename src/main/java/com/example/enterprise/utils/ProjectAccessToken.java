package com.example.enterprise.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class ProjectAccessToken {
    // without ip address 
    public String generateTokenFromUsernameintoClaims(String username, Map<String, Object> data, String key) {
        Integer id = (Integer) data.get("id");
        Key secretKey = key(key);
        return Jwts.builder()
                .subject((username))
                .claim("id", id)
                .claims(data)
                .issuedAt(new Date())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // with ip address
    public String generateTokenFromUsernamewithIp(String email, Map<String, Object> additionalData, long jwtExpirationMs,String key) throws UnknownHostException{
        // Retrieve server IP address from the request
        String serverIp = InetAddress.getLocalHost().getHostAddress();
        Key secretKey = key(key);
        return Jwts.builder()
                .subject(email)
                .claims(additionalData)
                .claim("serverIp", serverIp)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

      private Key key(String secretKey) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

}
