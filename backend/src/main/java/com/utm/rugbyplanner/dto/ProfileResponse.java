package com.utm.rugbyplanner.dto;

import com.utm.rugbyplanner.model.Trainer;
import com.utm.rugbyplanner.model.User;
import lombok.*;

import java.util.List;

/**
 * UC003: Update Profile — full combined response.
 * Returned by GET /api/profile and all PUT /api/profile/* endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    // ── User fields ────────────────────────────────────────
    private String        userId;
    private String        username;
    private String        fullName;
    private String        email;
    private String        phoneNumber;
    private String        profilePicture;   // base64 data URL
    private User.UserRole userRole;

    // ── Athlete fields (null when TRAINER) ─────────────────
    private String  athleteId;
    private Integer weight;
    private Integer targetWeight;
    private Integer height;
    private Integer age;
    private String  location;
    private String  goal;
    private String  activityLevel;
    private String  rugbyPosition;
    private String  dietaryRestrictions;
    private String  injuryNotes;
    private String  trainingLevel;

    // ── Trainer fields (null when ATHLETE) ─────────────────
    private String                          trainerId;
    private List<Trainer.AvailabilitySlot>  availabilitySlots;
    private String                          expertise;
    private String                          experience;
    private List<Trainer.CertificationFile> certificationFiles;
}