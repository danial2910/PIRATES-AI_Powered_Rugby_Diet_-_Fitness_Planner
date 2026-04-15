package com.utm.rugbyplanner.controller;

import com.utm.rugbyplanner.dto.*;
import com.utm.rugbyplanner.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * ProfileController — UC003: Update Profile
 *
 * All endpoints require a valid JWT Bearer token.
 * The authenticated username is extracted from the JWT via @AuthenticationPrincipal.
 *
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │  Method  │  URL                            │  Purpose               │
 * ├──────────┼─────────────────────────────────┼────────────────────────┤
 * │  GET     │  /api/profile                   │  Load current profile  │
 * │  PUT     │  /api/profile/user              │  Update name/email     │
 * │  PUT     │  /api/profile/athlete           │  Update athlete data   │
 * │  PUT     │  /api/profile/trainer           │  Update trainer data   │
 * └──────────────────────────────────────────────────────────────────────┘
 */
@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * GET /api/profile
     *
     * UC003 Normal Flow Step 2:
     * "System displays current profile information in editable form."
     *
     * Called when ProfileView.vue mounts — loads existing data into the form.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        ProfileResponse profile = profileService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile loaded", profile));
    }

    /**
     * PUT /api/profile/user
     *
     * Updates User document: fullName, email, phoneNumber.
     * Shared by both ATHLETE and TRAINER roles.
     *
     * 200 OK  → updated ProfileResponse
     * 409     → email already taken by another account
     * 422     → @Valid field errors
     */
    @PutMapping("/user")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateUserInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest request) {

        log.debug("UC003 updateUserInfo — username: {}", userDetails.getUsername());
        ProfileResponse updated = profileService.updateUserInfo(
                userDetails.getUsername(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Personal information updated successfully.", updated)
        );
    }

    /**
     * PUT /api/profile/athlete
     *
     * Updates Athlete document: weight, height, age, rugbyPosition,
     * goal, activityLevel, dietaryRestrictions, injuryNotes, trainingLevel.
     *
     * Only accessible to users with role ATHLETE.
     * Trainer attempting this will hit a 403 from Spring Security
     * if you add @PreAuthorize("hasRole('ATHLETE')") — currently open to
     * any authenticated user for simplicity.
     *
     * 200 OK  → updated ProfileResponse
     * 422     → @Valid field errors
     */
    @PutMapping("/athlete")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateAthleteProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateAthleteRequest request) {

        log.debug("UC003 updateAthleteProfile — username: {}", userDetails.getUsername());
        ProfileResponse updated = profileService.updateAthleteProfile(
                userDetails.getUsername(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Athlete profile updated successfully.", updated)
        );
    }

    /**
     * PUT /api/profile/trainer
     *
     * Updates Trainer document: availability, expertise, experience, certifications.
     *
     * 200 OK  → updated ProfileResponse
     * 422     → @Valid field errors
     */
    @PutMapping("/trainer")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateTrainerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateTrainerRequest request) {

        log.debug("UC003 updateTrainerProfile — username: {}", userDetails.getUsername());
        ProfileResponse updated = profileService.updateTrainerProfile(
                userDetails.getUsername(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Trainer profile updated successfully.", updated)
        );
    }
}