package com.utm.rugbyplanner.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Athlete — MongoDB collection: "athletes"
 *
 * Created automatically when a user registers with role = ATHLETE.
 * Linked 1-to-1 with User via userId.
 *
 * SDD Entity fields: weight, targetWeight, location, height,
 *   goal, age, activityLevel
 * Rugby-specific extensions: rugbyPosition, dietaryRestrictions,
 *   injuryNotes, trainingLevel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "athletes")
public class Athlete {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;         // FK → User._id

    // ── SDD fields ────────────────────────────────
    private Integer weight;        // kg
    private Integer targetWeight;  // kg
    private Integer height;        // cm
    private Integer age;
    private String  location;
    private String  goal;          // "STRENGTH" | "ENDURANCE" | "LEAN" | …
    private String  activityLevel; // "MODERATE" | "ACTIVE" | "EXTREME"

    // ── Rugby-specific ────────────────────────────
    private String  rugbyPosition;        // "Flanker", "Prop", "Fullback" …
    private String  dietaryRestrictions;  // e.g. "Halal only, no peanuts"
    private String  injuryNotes;          // e.g. "Mild left knee soreness"
    private String  trainingLevel;        // "BEGINNER" | "INTERMEDIATE" | …

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
