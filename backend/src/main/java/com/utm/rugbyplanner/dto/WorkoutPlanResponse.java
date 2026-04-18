package com.utm.rugbyplanner.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * WorkoutPlanResponse — returned by all /api/workout endpoints.
 *
 * Contains both the input parameters (for display in UI)
 * and the AI-generated plan text.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanResponse {

    private String  id;
    private String  userId;
    private String  planName;

    // ── Input parameters ─────────────────────────────────────
    private String  rugbyPosition;
    private String  goal;
    private String  trainingLevel;
    private Integer weight;
    private Integer height;
    private Integer age;
    private String  injuryNotes;
    private Integer sessionsPerWeek;

    // ── AI output ─────────────────────────────────────────────
    /** Full markdown text returned by Ollama llama3.2 */
    private String  generatedPlan;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
