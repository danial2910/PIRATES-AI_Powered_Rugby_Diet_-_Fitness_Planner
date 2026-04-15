package com.utm.rugbyplanner.service;

import com.utm.rugbyplanner.dto.LoginRequest;
import com.utm.rugbyplanner.dto.LoginResponse;
import com.utm.rugbyplanner.model.User;
import com.utm.rugbyplanner.repository.UserRepository;
import com.utm.rugbyplanner.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * UC001: Login — business logic.
 *
 * Normal Flow Steps 4 – 7 (SRS):
 *   Step 4: User clicks "Login"                   → controller calls login()
 *   Step 5: System verifies credentials            → authManager.authenticate()
 *   Step 6: System generates JWT + redirects       → jwtUtils.generateToken()
 *   Step 7: Welcome message on dashboard           → fullName in LoginResponse
 *
 * Alternative / Exception flows:
 *   AF1 Invalid credentials  → BadCredentialsException propagates to controller
 *   Disabled account         → DisabledException propagates to controller
 *   EF1 No internet          → handled on Vue side (Axios network error)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final JwtUtils              jwtUtils;

    public LoginResponse login(LoginRequest request) {
        log.debug("UC001 Login attempt — username: {}", request.getUsername());

        // Step 5 — Spring Security verifies username + BCrypt password against MongoDB
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Store auth in the security context for this request
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 6 — Generate the JWT that the Vue app will store and send back
        String jwt = jwtUtils.generateToken(authentication);

        // Fetch full User document for the response payload
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException(
                        "User not found after successful authentication — this should never happen"
                ));

        log.info("UC001 Login successful — user: {}, role: {}",
                user.getUsername(), user.getUserRole());

        // Step 7 — Return token + user info; Vue stores these in Pinia + localStorage
        return LoginResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();
    }
}
