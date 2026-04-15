package com.utm.rugbyplanner.controller;

import com.utm.rugbyplanner.dto.*;
import com.utm.rugbyplanner.service.AuthService;
import com.utm.rugbyplanner.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST controller.
 *
 * All endpoints live under /api/auth/** and are declared public
 * in SecurityConfig (no JWT required to reach them).
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Method  │  URL                   │  Use Case              │
 * ├──────────┼────────────────────────┼────────────────────────┤
 * │  POST    │  /api/auth/login       │  UC001: Login          │
 * │  POST    │  /api/auth/register    │  UC002: Register       │
 * │  GET     │  /api/auth/verify      │  Vue router guard      │
 * │  POST    │  /api/auth/logout      │  Clear server log      │
 * └─────────────────────────────────────────────────────────────┘
 *
 * All error responses are handled centrally by GlobalExceptionHandler.
 * BadCredentials / Disabled are caught here so the response body stays
 * as ApiResponse rather than Spring Security's default HTML error page.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService     authService;
    private final RegisterService registerService;

    // ── UC001: Login ──────────────────────────────────────────────────────────

    /**
     * POST /api/auth/login
     *
     * Request:  { "username": "danial", "password": "Password123" }
     *
     * 200 OK   → { success: true,  data: { accessToken, userId, username, fullName, userRole, email } }
     * 400      → @Valid failure (blank fields)
     * 401      → AF1 invalid credentials  |  account disabled
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(
                    ApiResponse.success("Login successful. Welcome back!", response)
            );
        } catch (BadCredentialsException e) {
            log.warn("UC001 AF1 — bad credentials for username: {}", request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(
                            "Invalid username or password. Please try again."
                    ));
        } catch (DisabledException e) {
            log.warn("UC001 — disabled account: {}", request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(
                            "Your account has been disabled. Please contact the administrator."
                    ));
        }
    }

    // ── UC002: Register ───────────────────────────────────────────────────────

    /**
     * POST /api/auth/register
     *
     * Request:  { fullName, email, phoneNumber?, username, password,
     *             confirmPassword, userRole }
     *
     * 201 Created → { success: true, data: { userId, username, fullName, email, userRole } }
     * 400         → AF1 password mismatch   (ValidationException)
     * 409         → AF2 duplicate email/username  (DuplicateResourceException)
     * 422         → AF1 @Valid field errors  (MethodArgumentNotValidException)
     *
     * GlobalExceptionHandler converts 400 / 409 / 422 automatically.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = registerService.register(request);

        log.info("UC002 Registration successful — username: {}", response.getUsername());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Account created successfully! You can now sign in.",
                        response
                ));
    }

    // ── Token management ──────────────────────────────────────────────────────

    /**
     * GET /api/auth/verify
     *
     * Used by the Vue router's navigation guard to check whether the
     * stored JWT is still valid before rendering a protected page.
     * The JwtAuthenticationFilter already validated the token; if we
     * reach here the token is good.
     *
     * 200 OK  → token is valid
     * 401     → token expired / invalid (rejected by filter before reaching here)
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyToken() {
        return ResponseEntity.ok(ApiResponse.success("Token is valid", null));
    }

    /**
     * POST /api/auth/logout
     *
     * JWT is stateless — the server has nothing to invalidate.
     * This endpoint exists for logging / audit purposes.
     * The real logout happens client-side: Vue clears localStorage.
     *
     * 200 OK always
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
