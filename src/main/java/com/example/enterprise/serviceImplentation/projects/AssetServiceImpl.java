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
import com.example.enterprise.utils.ProjectAccessToken;

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
    private final ProjectAccessToken projectAccessToken;
    @Value("${enterprise.app.jwtExpirationMs}")
    private long jwtExpirationMs;
    private static final String SECRET_KEY = "======================AssetManagement==============================================";
    private final AuthUserDetails authUserDetails;

    @Override
    public Map<String, Object> assetSignIn(String projectId, HttpServletRequest request) throws UnknownHostException {
        // try {

            Map<String, Object> response = new HashMap<>();

            String userId = AuthUserDetails.getUserId();
            String userEmail = authUserDetails.getUserDetailsFromJwt(request);

            Optional<Document> userDetailsOpt = fetchUserDetails(userId, projectId);

            if (userDetailsOpt.isEmpty()) {
                response.put("error","User details not found");
                return response;
            }

            Document userDetails = userDetailsOpt.get();

            Map<String, Object> userDataMap = buildUserDataMap(userDetails, request);

            String jwt = projectAccessToken.generateTokenFromUsernamewithIp(userEmail, userDataMap,jwtExpirationMs,SECRET_KEY);

            // Prepare response object
            // AssetResponse userResponse = new AssetResponse();
            // userResponse.setToken(jwt);
            response.put("token", jwt);
            return response;

        // } catch (UsernameNotFoundException | BadCredentialsException e) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
        //             .body(new ApiResponse(false, "Invalid Credentials"));
        // } catch (Exception e) {
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        //             .body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        // }
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

}
