package com.utm.rugbyplanner.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * WorkoutPlan — MongoDB collection: "workout_plans"
 *
 * UC004: Create Workout Plan
 * UC005: Manage Workout Plan
 *
 * Each document stores one AI-generated weekly workout plan
 * linked to a User via userId. The raw AI response is stored
 * in generatedPlan (formatted markdown text from Ollama).
 *
 * Inputs used to generate the plan:
 *   - rugbyPosition    → determines training focus (strength / speed / agility)
 *   - goal             → STRENGTH | ENDURANCE | LEAN | POWER
 *   - trainingLevel    → BEGINNER | INTERMEDIATE | ADVANCED
 *   - weight, height, age → physical stats for volume/intensity tuning
 *   - injuryNotes      → AI avoids exercises that aggravate existing injuries
 *   - sessionsPerWeek  → number of training days (3–6)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workout_plans")
public class WorkoutPlan {

    @Id
    private String id;

    @Indexed
    private String userId;          // FK → User._id

    /** User-defined name for this plan (e.g. "Pre-Season Strength Block") */
    private String planName;

    // ── Input parameters sent to Ollama ──────────────────────────────────
    private String  rugbyPosition;   // "Prop", "Flanker", "Fullback", etc.
    private String  goal;            // "STRENGTH" | "ENDURANCE" | "LEAN" | "POWER"
    private String  trainingLevel;   // "BEGINNER" | "INTERMEDIATE" | "ADVANCED"
    private Integer weight;          // kg
    private Integer height;          // cm
    private Integer age;             // years
    private String  injuryNotes;     // free text, nullable
    private Integer sessionsPerWeek; // 3 – 6

    /** Full AI-generated plan text (markdown from Ollama llama3.2) */
    private String generatedPlan;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
