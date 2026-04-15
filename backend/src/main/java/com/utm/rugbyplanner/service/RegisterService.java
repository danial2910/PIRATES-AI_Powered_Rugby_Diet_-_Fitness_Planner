package com.utm.rugbyplanner.service;

import com.utm.rugbyplanner.dto.RegisterRequest;
import com.utm.rugbyplanner.dto.RegisterResponse;
import com.utm.rugbyplanner.exception.DuplicateResourceException;
import com.utm.rugbyplanner.exception.ValidationException;
import com.utm.rugbyplanner.model.Athlete;
import com.utm.rugbyplanner.model.Trainer;
import com.utm.rugbyplanner.model.User;
import com.utm.rugbyplanner.repository.AthleteRepository;
import com.utm.rugbyplanner.repository.TrainerRepository;
import com.utm.rugbyplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UC002: Register — business logic.
 *
 * Normal Flow Steps 6 – 9 (SRS):
 *   Step 7: System validates all entered information   → password match check + @Valid
 *   Step 8: System checks email is not registered yet → existsByEmail / existsByUsername
 *   Step 9: System activates the account              → save User + create profile doc
 *
 * After registration the Vue app redirects to /login with a success banner.
 * The user then completes UC001 (Login) to access their dashboard.
 *
 * Alternative Flows:
 *   AF1 Missing/invalid data  → @Valid annotations + ValidationException
 *   AF2 Duplicate email/user  → DuplicateResourceException (HTTP 409)
 *
 * SDD Composition rule:
 *   User (1..1) ← → Athlete (0..1)   when userRole == ATHLETE
 *   User (1..1) ← → Trainer (0..1)   when userRole == TRAINER
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository    userRepository;
    private final AthleteRepository athleteRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder   passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest req) {

        log.debug("UC002 Register — username: {}, email: {}, role: {}",
                req.getUsername(), req.getEmail(), req.getUserRole());

        // ── Step 7a: password confirmation match ──────────────────────────────
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match. Please try again.");
        }

        // ── Step 8: duplicate email check (UC002 AF2) ─────────────────────────
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException(
                    "This email address is already registered. " +
                    "Please sign in or use a different email."
            );
        }

        // ── Step 8: duplicate username check (UC002 AF2 variant) ──────────────
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new DuplicateResourceException(
                    "This username is already taken. Please choose a different one."
            );
        }

        // ── Step 9a: create User document ────────────────────────────────────
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))  // BCrypt hash
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber())
                .userRole(req.getUserRole())
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("UC002 User created — id: {}, username: {}, role: {}",
                saved.getId(), saved.getUsername(), saved.getUserRole());

        // ── Step 9b: create role-specific profile document ────────────────────
        if (req.getUserRole() == User.UserRole.ATHLETE) {
            createAthleteProfile(saved.getId());
        } else {
            createTrainerProfile(saved.getId());
        }

        return RegisterResponse.builder()
                .userId(saved.getId())
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .userRole(saved.getUserRole())
                .message("Account created successfully! You can now sign in.")
                .build();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Creates a blank Athlete document linked to the new User.
     * Additional fields (position, weight, goals …) are filled in
     * later by UC003: Update Profile.
     */
    private void createAthleteProfile(String userId) {
        athleteRepository.save(
                Athlete.builder().userId(userId).build()
        );
        log.debug("UC002 Athlete profile created for userId: {}", userId);
    }

    /**
     * Creates a blank Trainer document linked to the new User.
     * Additional fields (expertise, certifications …) are filled in
     * later by UC003: Update Profile.
     */
    private void createTrainerProfile(String userId) {
        trainerRepository.save(
                Trainer.builder().userId(userId).build()
        );
        log.debug("UC002 Trainer profile created for userId: {}", userId);
    }
}
