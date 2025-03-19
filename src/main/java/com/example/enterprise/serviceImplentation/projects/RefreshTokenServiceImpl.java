package com.example.enterprise.serviceImplentation.projects;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.entity.RefreshToken;
import com.example.enterprise.entity.User;
import com.example.enterprise.entity.UserRefreshToken;
import com.example.enterprise.repository.RefreshTokenRepository;
import com.example.enterprise.repository.UserRefreshTokenRepository;
import com.example.enterprise.repository.UserRepository;
import com.example.enterprise.repository.EnterPriseUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final EnterPriseUserRepository enterPriseUserRepository;
    private final UserRepository userRepository;

    @Value("${enterprise.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByUserToken(String token) {
        return userRefreshTokenRepository.findByToken(token);
    }


    public RefreshToken createRefreshToken(String userId) {

        Optional<RefreshToken> existingTokenOptional = refreshTokenRepository.findByUsers(userId);

        if (existingTokenOptional.isPresent()) {
            RefreshToken existingToken = existingTokenOptional.get();
            existingToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return refreshTokenRepository.save(existingToken);
        } else {

            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setUsers(enterPriseUserRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")));
            newRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            newRefreshToken.setToken(UUID.randomUUID().toString());
            return refreshTokenRepository.save(newRefreshToken);
        }
    }

    public boolean isValid(RefreshToken token) {
        return token.getExpiryDate().isAfter(Instant.now());
    }

    public String deleteByenterpriseUserId(String userId) {
        Optional<EnterPriseUser> userOptional = enterPriseUserRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            return refreshTokenRepository.deleteByUsers(userOptional.get());
        }
        return null;
    }

    public String deleteByUserId(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            return userRefreshTokenRepository.deleteByUsers(userOptional.get());
        }
        return null;
    }



    public UserRefreshToken createUserRefreshToken(String userId) {

        Optional<UserRefreshToken> existingTokenOptional = userRefreshTokenRepository.findByUsers(userId);

        if (existingTokenOptional.isPresent()) {
            UserRefreshToken existingToken = existingTokenOptional.get();
            existingToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return userRefreshTokenRepository.save(existingToken);
        } else {

            UserRefreshToken newRefreshToken = new UserRefreshToken();
            newRefreshToken.setUsers(userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")));
            newRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            newRefreshToken.setToken(UUID.randomUUID().toString());
            return userRefreshTokenRepository.save(newRefreshToken);
        }
    }
}
