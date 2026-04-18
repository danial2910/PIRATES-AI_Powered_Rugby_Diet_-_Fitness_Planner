package com.utm.rugbyplanner.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * WorkoutPlanRequest — body for POST /api/workout/generate
 *
 * All fields feed into the Ollama prompt for UC004: Create Workout Plan.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanRequest {

    /** Optional custom name. Defaults to "My Workout Plan" if blank. */
    private String planName;

    @NotBlank(message = "Rugby position is required.")
    private String rugbyPosition;

    @NotBlank(message = "Fitness goal is required.")
    private String goal;            // STRENGTH | ENDURANCE | LEAN | POWER

    @NotBlank(message = "Training level is required.")
    private String trainingLevel;   // BEGINNER | INTERMEDIATE | ADVANCED

    @NotNull(message = "Weight is required.")
    @Min(value = 40, message = "Weight must be at least 40 kg.")
    @Max(value = 200, message = "Weight must be at most 200 kg.")
    private Integer weight;

    @NotNull(message = "Height is required.")
    @Min(value = 140, message = "Height must be at least 140 cm.")
    @Max(value = 220, message = "Height must be at most 220 cm.")
    private Integer height;

    @NotNull(message = "Age is required.")
    @Min(value = 15, message = "Age must be at least 15.")
    @Max(value = 60, message = "Age must be at most 60.")
    private Integer age;

    /** Optional — e.g. "Mild left knee soreness". Nullable is fine. */
    private String injuryNotes;

    @NotNull(message = "Sessions per week is required.")
    @Min(value = 2, message = "Minimum 2 sessions per week.")
    @Max(value = 7, message = "Maximum 7 sessions per week.")
    private Integer sessionsPerWeek;
}
