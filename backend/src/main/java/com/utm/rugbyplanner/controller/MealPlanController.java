package com.utm.rugbyplanner.controller;

import com.utm.rugbyplanner.dto.ApiResponse;
import com.utm.rugbyplanner.dto.MealPlanRequest;
import com.utm.rugbyplanner.dto.MealPlanResponse;
import com.utm.rugbyplanner.service.MealPlanService;
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
 * MealPlanController
 *
 * UC006: Create Meal Plan
 * UC007: Manage Meal Plan
 *
 * All endpoints require a valid JWT Bearer token.
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  Method  │  URL                        │  Purpose                       │
 * ├──────────┼─────────────────────────────┼────────────────────────────────┤
 * │  POST    │  /api/meal/generate         │  UC006 Generate new plan       │
 * │  GET     │  /api/meal/plans            │  UC007 List all saved plans    │
 * │  GET     │  /api/meal/plans/{id}       │  UC007 Get a specific plan     │
 * │  DELETE  │  /api/meal/plans/{id}       │  UC007 Delete a plan           │
 * └──────────────────────────────────────────────────────────────────────────┘
 */
@Slf4j
@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    // ── UC006: Generate ───────────────────────────────────────────────────────

    /**
     * POST /api/meal/generate
     *
     * 201 Created → MealPlanResponse with the AI-generated 7-day plan
     * 400         → @Valid field errors
     * 503         → Ollama not running / model not pulled
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<MealPlanResponse>> generatePlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MealPlanRequest request) {

        log.debug("UC006 Generate meal plan — user: {}", userDetails.getUsername());

        try {
            MealPlanResponse response =
                    mealPlanService.generatePlan(userDetails.getUsername(), request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Meal plan generated successfully!", response));

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("AI engine is not available")) {
                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(ApiResponse.error(e.getMessage()));
            }
            throw e;
        }
    }

    // ── UC007: List plans ─────────────────────────────────────────────────────

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<MealPlanResponse>>> getPlans(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<MealPlanResponse> plans =
                mealPlanService.getPlans(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Plans loaded successfully.", plans));
    }

    // ── UC007: Get single plan ────────────────────────────────────────────────

    @GetMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<MealPlanResponse>> getPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {

        MealPlanResponse plan = mealPlanService.getPlan(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Plan loaded.", plan));
    }

    // ── UC007: Delete plan ────────────────────────────────────────────────────

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {

        mealPlanService.deletePlan(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Meal plan deleted successfully.", null));
    }
}
