package com.utm.rugbyplanner.controller;

import com.utm.rugbyplanner.dto.ApiResponse;
import com.utm.rugbyplanner.dto.WorkoutPlanRequest;
import com.utm.rugbyplanner.dto.WorkoutPlanResponse;
import com.utm.rugbyplanner.service.WorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WorkoutPlanController
 *
 * UC004: Create Workout Plan
 * UC005: Manage Workout Plan
 *
 * All endpoints require a valid JWT Bearer token.
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  Method  │  URL                          │  Purpose                     │
 * ├──────────┼───────────────────────────────┼──────────────────────────────┤
 * │  POST    │  /api/workout/generate        │  UC004 Generate new plan     │
 * │  GET     │  /api/workout/plans           │  UC005 List all saved plans  │
 * │  GET     │  /api/workout/plans/{id}      │  UC005 Get a specific plan   │
 * │  DELETE  │  /api/workout/plans/{id}      │  UC005 Delete a plan         │
 * └──────────────────────────────────────────────────────────────────────────┘
 */
@Slf4j
@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    // ── UC004: Generate ───────────────────────────────────────────────────────

    /**
     * POST /api/workout/generate
     *
     * Builds a prompt from the request body, calls Ollama llama3.2,
     * and saves the resulting plan to MongoDB.
     *
     * 201 Created → WorkoutPlanResponse with the AI-generated plan text
     * 400         → @Valid field errors
     * 503         → Ollama not running / model not pulled
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<WorkoutPlanResponse>> generatePlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WorkoutPlanRequest request) {

        log.debug("UC004 Generate workout plan — user: {}", userDetails.getUsername());

        try {
            WorkoutPlanResponse response =
                    workoutPlanService.generatePlan(userDetails.getUsername(), request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Workout plan generated successfully!", response));

        } catch (RuntimeException e) {
            // Catch Ollama connectivity errors and return a friendly 503
            if (e.getMessage() != null && e.getMessage().contains("AI engine is not available")) {
                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(ApiResponse.error(e.getMessage()));
            }
            throw e;
        }
    }

    // ── UC005: List plans ─────────────────────────────────────────────────────

    /**
     * GET /api/workout/plans
     *
     * Returns all saved workout plans for the authenticated user,
     * ordered by creation date (newest first).
     *
     * 200 OK → List<WorkoutPlanResponse>
     */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<WorkoutPlanResponse>>> getPlans(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<WorkoutPlanResponse> plans =
                workoutPlanService.getPlans(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.success("Plans loaded successfully.", plans));
    }

    // ── UC005: Get single plan ────────────────────────────────────────────────

    /**
     * GET /api/workout/plans/{id}
     *
     * 200 OK → WorkoutPlanResponse
     * 404    → Plan not found or doesn't belong to this user
     */
    @GetMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<WorkoutPlanResponse>> getPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {

        WorkoutPlanResponse plan =
                workoutPlanService.getPlan(userDetails.getUsername(), id);

        return ResponseEntity.ok(ApiResponse.success("Plan loaded.", plan));
    }

    // ── UC005: Delete plan ────────────────────────────────────────────────────

    /**
     * DELETE /api/workout/plans/{id}
     *
     * 200 OK → success message
     * 404    → Plan not found or doesn't belong to this user
     */
    @DeleteMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {

        workoutPlanService.deletePlan(userDetails.getUsername(), id);

        return ResponseEntity.ok(
                ApiResponse.success("Workout plan deleted successfully.", null));
    }
}
