package com.utm.rugbyplanner.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * MealPlanResponse — returned by all /api/meal endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanResponse {

    private String  id;
    private String  userId;
    private String  planName;

    // ── Input parameters ─────────────────────────────────────
    private String  rugbyPosition;
    private String  goal;
    private Integer weight;
    private Integer height;
    private Integer age;
    private String  dietaryPreference;
    private String  allergies;
    private Integer mealsPerDay;

    // ── AI output ─────────────────────────────────────────────
    /** Full 7-day markdown meal plan returned by Ollama llama3.2 */
    private String  generatedPlan;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
