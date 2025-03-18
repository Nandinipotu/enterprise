package com.example.enterprise.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {
    private String token;
    private Date expirationTime;
    private String refreshToken;
    private String refreshTokenExpiryTime;
}