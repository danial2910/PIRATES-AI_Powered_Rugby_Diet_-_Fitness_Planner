package com.utm.rugbyplanner.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * UC003: Update Profile — Athlete-specific fields.
 *
 * Maps to SDD Athlete entity:
 *   weight, targetWeight, height, age, location,
 *   goal, activityLevel
 *
 * Rugby-specific extensions (from thesis requirements):
 *   rugbyPosition, dietaryRestrictions, injuryNotes, trainingLevel
 */
@Data
public class UpdateAthleteRequest {

    // ── SDD Entity fields ──────────────────────────────────

    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 250, message = "Weight cannot exceed 250 kg")
    private Integer weight;

    @Min(value = 30, message = "Target weight must be at least 30 kg")
    @Max(value = 250, message = "Target weight cannot exceed 250 kg")
    private Integer targetWeight;

    @Min(value = 100, message = "Height must be at least 100 cm")
    @Max(value = 250, message = "Height cannot exceed 250 cm")
    private Integer height;

    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 80, message = "Age cannot exceed 80")
    private Integer age;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    // goal: STRENGTH | ENDURANCE | LEAN | BULK | RECOVERY
    @Size(max = 50)
    private String goal;

    // activityLevel: MODERATE | ACTIVE | EXTREME
    @Size(max = 50)
    private String activityLevel;

    // ── Rugby-specific fields ──────────────────────────────

    // e.g. "Flanker", "Prop", "Fullback", "Scrum Half"
    @Size(max = 50, message = "Rugby position cannot exceed 50 characters")
    private String rugbyPosition;

    // e.g. "Halal only, no peanuts, lactose intolerant"
    @Size(max = 300, message = "Dietary restrictions cannot exceed 300 characters")
    private String dietaryRestrictions;

    // e.g. "Mild left knee soreness"
    @Size(max = 300, message = "Injury notes cannot exceed 300 characters")
    private String injuryNotes;

    // BEGINNER | INTERMEDIATE | ADVANCED | ELITE
    @Size(max = 50)
    private String trainingLevel;
}