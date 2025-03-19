package com.example.enterprise.serviceImplentation.projects;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import com.example.enterprise.dto.response.AssetResponse;
import com.example.enterprise.entity.RefreshToken;

import com.example.enterprise.service.project.AssetService;
import com.example.enterprise.utils.ApiResponse;
import com.example.enterprise.utils.AuthUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final MongoTemplate mongoTemplate;

    @Value("${enterprise.app.jwtExpirationMs}")
    private long jwtExpirationMs;
    private static final String SECRET_KEY = "======================AssetManagement==============================================";

    private final AuthUserDetails authUserDetails;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    @Override
    public ResponseEntity<ApiResponse> assetSignIn(String projectId, HttpServletRequest request) {
        try {

            String userId = AuthUserDetails.getUserId();
            String userEmail = authUserDetails.getUserDetailsFromJwt(request);

            Optional<Document> userDetailsOpt = fetchUserDetails(userId, projectId);

            if (userDetailsOpt.isEmpty()) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "User details not found"));
            }

            Document userDetails = userDetailsOpt.get();

            Map<String, Object> userDataMap = buildUserDataMap(userDetails, request);

            // // Generate refresh token
            // RefreshToken refreshToken = assetRefreshToken.createRefreshToken(userId);
            // Instant expiryInstant = refreshToken.getExpiryDate();
            // LocalDateTime expiryDateTime = LocalDateTime.ofInstant(expiryInstant, ZoneId.systemDefault());
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // String formattedExpiryTime = expiryDateTime.format(formatter);

            // // Add refresh token details to user data map
            // userDataMap.put("refreshToken", refreshToken.getToken());
            // userDataMap.put("refreshTokenExpiryTime", formattedExpiryTime);

            String jwt = generateJwtToken(userEmail, userDataMap);
            Date expirationTime = getExpirationDateFromJwtToken(jwt);

            // Prepare response object
            AssetResponse userResponse = new AssetResponse();
            userResponse.setExpirationTime(expirationTime);
            userResponse.setToken(jwt);
            // userResponse.setRefreshToken(refreshToken.getToken());
            // userResponse.setRefreshTokenExpiryTime(formattedExpiryTime);

            return ResponseEntity.ok(new ApiResponse(true, "User Login Successfully", List.of(userResponse)));

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Invalid Credentials"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        }
    }

    private Optional<Document> fetchUserDetails(String userId, String projectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("projectId").is(projectId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Document.class, "enterprise_user_projects"));
    }

    private Map<String, Object> buildUserDataMap(Document userDetails, HttpServletRequest request) {
        List<Document> requiredFieldsList = userDetails.getList("requiredFields", Document.class);

        if (requiredFieldsList == null || requiredFieldsList.isEmpty()) {
            throw new IllegalStateException("Required fields are missing in user details");
        }

        Document requiredFields = requiredFieldsList.get(0);

        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("id", requiredFields.getString("id"));
        userDataMap.put("domain", requiredFields.getString("domain"));
        userDataMap.put("phoneNo", requiredFields.getString("phoneNo"));
        userDataMap.put("plant", requiredFields.getString("plant"));
        userDataMap.put("role", userDetails.getString("role"));
        userDataMap.put("userId", requiredFields.getString("userId"));
        userDataMap.put("username", requiredFields.getString("userName"));
        userDataMap.put("email", authUserDetails.getUserDetailsFromJwt(request)); // Email is at root level
        userDataMap.put("profile", userDetails.getString("pictureWithPath")); // Assuming from root
        userDataMap.put("companyId", requiredFields.getString("companyId"));

        return userDataMap;
    }

    public String generateJwtToken(String email, Map<String, Object> additionalData) throws UnknownHostException {
        // Retrieve server IP address from the request
        String serverIp = InetAddress.getLocalHost().getHostAddress();

        return Jwts.builder()
                .subject(email)
                .claims(additionalData)
                .claim("serverIp", serverIp)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshJwtToken(String email, Map<String, Object> additionalData) {
        // Retrieve server IP address from the request
        String serverIp = getServerIpAddress();

        return Jwts.builder()
                .subject(email)
                .claims(additionalData)
                .claim("serverIp", serverIp)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String getServerIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null; 
        }
    }

    public Date getExpirationDateFromJwtToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

}
