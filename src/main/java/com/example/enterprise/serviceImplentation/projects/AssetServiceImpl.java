package com.example.enterprise.serviceImplentation.projects;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
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
            response.put("error", "User details not found");
            return response;
        }

        Document userDetails = userDetailsOpt.get();

        Map<String, Object> userDataMap = buildUserDataMap(userDetails, request);

        String jwt = projectAccessToken.generateTokenFromUsernamewithIp(userEmail, userDataMap, jwtExpirationMs,
                SECRET_KEY);
        Criteria criteria = Criteria.where("userId").is(userId).and("projectId").is(projectId);
        LookupOperation projectLookup = Aggregation.lookup("projects", "projectId", "projectId", "projectDetails");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                projectLookup,
                Aggregation.unwind("projectDetails", true),
                Aggregation.project("projectDetails.projectURL").andExclude("_id"));
        Map<String, String> result = mongoTemplate.aggregate(aggregation, "enterprise_user_projects", Map.class)
                .getUniqueMappedResult();
        response.put("token", jwt);
        response.put("url", result.get("projectURL"));
        return response;

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
        System.out.println("requiredFields  :" + requiredFields);
        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("id", requiredFields.getString("id"));
        userDataMap.put("domain", requiredFields.getString("domain"));
        userDataMap.put("phoneNo", requiredFields.getString("phoneNo"));
        userDataMap.put("plant", requiredFields.getString("plant"));
        // userDataMap.put("role", userDetails.get("role"));
        List<Document> roles = (List<Document>) requiredFields.get("role");

        List<Map<String, String>> roleList = new ArrayList<>();

        if (roles != null && !roles.isEmpty()) {
            for (Document roleDoc : roles) {
                Object authorityObj = roleDoc.get("authority"); 

                if (authorityObj instanceof String) {
                    // If it's a single string, wrap it in a map
                    Map<String, String> roleMap = new HashMap<>();
                    roleMap.put("authority", (String) authorityObj);
                    roleList.add(roleMap);
                } else if (authorityObj instanceof List) {
                    // If it's a list, iterate over it
                    List<String> authorities = (List<String>) authorityObj;
                    for (String authority : authorities) {
                        Map<String, String> roleMap = new HashMap<>();
                        roleMap.put("authority", authority);
                        roleList.add(roleMap);
                    }
                }
            }
        }

        userDataMap.put("role", roleList);

        userDataMap.put("userId", requiredFields.getString("userId"));
        userDataMap.put("username", requiredFields.getString("username"));
        userDataMap.put("email", authUserDetails.getUserDetailsFromJwt(request)); // Email is at root level
        userDataMap.put("profile", userDetails.getString("pictureWithPath")); // Assuming from root
        userDataMap.put("companyId", requiredFields.getString("companyId"));

        return userDataMap;
    }

}
