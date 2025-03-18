package com.example.enterprise.serviceImplentation.projects.asset;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.entity.RefreshToken;
import com.example.enterprise.repository.AssetRefreshTokenRepository;
import com.example.enterprise.repository.EnterPriseUserRepository;
import com.example.enterprise.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetRefreshToken {
    private final AssetRefreshTokenRepository refreshTokenRepository;
    private final EnterPriseUserRepository userRepository;
    
    @Value("${enterprise.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken createRefreshToken(String userId) {

        Optional<RefreshToken> existingTokenOptional = refreshTokenRepository.findByUsers(userId);

        if (existingTokenOptional.isPresent()) {
            RefreshToken existingToken = existingTokenOptional.get();
            existingToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return refreshTokenRepository.save(existingToken);
        } else {

            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setUsers(userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("User not found")));
            newRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            newRefreshToken.setToken(UUID.randomUUID().toString());
            return refreshTokenRepository.save(newRefreshToken);
        }
    }

}
