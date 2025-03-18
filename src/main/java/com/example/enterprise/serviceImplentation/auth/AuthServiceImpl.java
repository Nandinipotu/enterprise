package com.example.enterprise.serviceImplentation.auth;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.enterprise.dto.request.LoginRequest;
import com.example.enterprise.dto.request.SignOutRequest;
import com.example.enterprise.dto.request.SignUpRequest;
import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.entity.User;
import com.example.enterprise.jwt.JwtUtils;
import com.example.enterprise.repository.EnterPriseUserRepository;
import com.example.enterprise.repository.UserRepository;
import com.example.enterprise.service.auth.AuthService;
import com.example.enterprise.utils.ApiResponse;
import com.example.enterprise.utils.ResponseMessage;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EnterPriseUserRepository enterPriseUserRepo;

    @Override
    public ResponseEntity<ApiResponse> signUp(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessage.USER_NOT_FOUND.getMessage()));
        }

        User existingUser = optionalUser.get();

        if (existingUser.isUserLoggedIn()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, ResponseMessage.USER_ALREADY_LOGGED_IN.getMessage()));
        }

        existingUser.setUserLoggedIn(true);
        existingUser.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        existingUser.setActive(true);
        existingUser.setUpdatedAt(new Date());

        userRepository.save(existingUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, ResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage()));
    }

    @Override
    public ResponseEntity<ApiResponse> logIn(LoginRequest loginRequest, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException(ResponseMessage.INVALID_CREDENTIALS.getMessage()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(ResponseMessage.INVALID_CREDENTIALS.getMessage());
        }

        String jwt = jwtUtils.generateToken(user);
        Cookie cookies = new Cookie("jwtToken", jwt);
        cookies.setMaxAge(60 * 60 * 24);
        cookies.setHttpOnly(true);
        cookies.setSecure(true);
        cookies.setPath("/");
        cookies.setAttribute("SameSite", "Lax");
        response.addCookie(cookies);

        Map<String, Object> authToken = new LinkedHashMap<>();
        authToken.put("token", jwt);

        return ResponseEntity.ok()
                .body(new ApiResponse(true, ResponseMessage.USER_LOGGED_IN_SUCCESS.getMessage(), authToken));
    }

    @Override
    public ResponseEntity<ApiResponse> signOut(SignOutRequest request) {
        jwtUtils.invalidateToken(request.getToken());
        return ResponseEntity.ok()
                .body(new ApiResponse(true, ResponseMessage.USER_LOGGED_OUT_SUCCESSFULLY.getMessage()));
    }

    @Override
    public ResponseEntity<ApiResponse> enterpriseUserSignUp(LoginRequest loginRequest) {
        Optional<EnterPriseUser> optionalUser = enterPriseUserRepo.findByUserEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessage.USER_NOT_FOUND.getMessage()));
        }

        EnterPriseUser existingUser = optionalUser.get();

        if (existingUser.isUserLoggedIn()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, ResponseMessage.USER_ALREADY_LOGGED_IN.getMessage()));
        }

        existingUser.setUserLoggedIn(true);
        existingUser.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        existingUser.setActive(true);
        existingUser.setUpdatedAt(new Date());

        enterPriseUserRepo.save(existingUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, ResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage()));
    }

    @Override
    public ResponseEntity<ApiResponse> enterpriseUserLogIn(LoginRequest loginRequest, HttpServletResponse response) {
        EnterPriseUser user = enterPriseUserRepo.findByUserEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException(ResponseMessage.INVALID_CREDENTIALS.getMessage()));

        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(ResponseMessage.INVALID_CREDENTIALS.getMessage());
        }

        String jwt = jwtUtils.generateEnterpriseUserToken(user);
        Cookie cookies = new Cookie("jwtToken", jwt);
        cookies.setMaxAge(60 * 60 * 24);
        cookies.setHttpOnly(true);
        cookies.setSecure(true);
        cookies.setPath("/");
        cookies.setAttribute("SameSite", "Lax");
        response.addCookie(cookies);

        Map<String, Object> authToken = new LinkedHashMap<>();
        authToken.put("token", jwt);

        return ResponseEntity.ok()
                .body(new ApiResponse(true, ResponseMessage.USER_LOGGED_IN_SUCCESS.getMessage(), authToken));
    }

}
