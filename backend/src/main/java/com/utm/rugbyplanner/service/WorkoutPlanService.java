package com.utm.rugbyplanner.service;

import com.utm.rugbyplanner.dto.WorkoutPlanRequest;
import com.utm.rugbyplanner.dto.WorkoutPlanResponse;
import com.utm.rugbyplanner.model.User;
import com.utm.rugbyplanner.model.WorkoutPlan;
import com.utm.rugbyplanner.repository.UserRepository;
import com.utm.rugbyplanner.repository.WorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WorkoutPlanService — UC004: Create Workout Plan / UC005: Manage Workout Plan
 *
 * Responsibilities:
 *   1. Build a rugby-specific prompt from the user's inputs.
 *   2. Call OllamaService to generate the plan text.
 *   3. Persist the plan to MongoDB.
 *   4. Provide list / delete operations for UC005.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final UserRepository        userRepository;
    private final OllamaService         ollamaService;

    // ── UC004: Generate a new workout plan ───────────────────────────────────

    public WorkoutPlanResponse generatePlan(String username, WorkoutPlanRequest req) {
        User user = findUser(username);

        log.info("UC004 Generate workout plan — user: {}, position: {}, sessions: {}",
                username, req.getRugbyPosition(), req.getSessionsPerWeek());

        // Build rugby-specific prompt
        String prompt = buildPrompt(req);

        // Call Ollama llama3.2
        String generatedPlan = ollamaService.generate(prompt);

        // Persist to MongoDB
        String planName = (req.getPlanName() != null && !req.getPlanName().isBlank())
                ? req.getPlanName()
                : req.getRugbyPosition() + " – " + req.getGoal() + " Plan";

        WorkoutPlan plan = WorkoutPlan.builder()
                .userId(user.getId())
                .planName(planName)
                .rugbyPosition(req.getRugbyPosition())
                .goal(req.getGoal())
                .trainingLevel(req.getTrainingLevel())
                .weight(req.getWeight())
                .height(req.getHeight())
                .age(req.getAge())
                .injuryNotes(req.getInjuryNotes())
                .sessionsPerWeek(req.getSessionsPerWeek())
                .generatedPlan(generatedPlan)
                .build();

        WorkoutPlan saved = workoutPlanRepository.save(plan);
        log.info("UC004 Workout plan saved — id: {}", saved.getId());

        return toResponse(saved);
    }

    // ── UC005: List saved plans ───────────────────────────────────────────────

    public List<WorkoutPlanResponse> getPlans(String username) {
        User user = findUser(username);
        return workoutPlanRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── UC005: Get single plan ────────────────────────────────────────────────

    public WorkoutPlanResponse getPlan(String username, String planId) {
        User user = findUser(username);
        WorkoutPlan plan = workoutPlanRepository
                .findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new RuntimeException("Workout plan not found."));
        return toResponse(plan);
    }

    // ── UC005: Delete a plan ──────────────────────────────────────────────────

    public void deletePlan(String username, String planId) {
        User user = findUser(username);
        WorkoutPlan plan = workoutPlanRepository
                .findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new RuntimeException("Workout plan not found."));
        workoutPlanRepository.delete(plan);
        log.info("UC005 Workout plan deleted — id: {}", planId);
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    /**
     * Builds a detailed, rugby-specific prompt for Ollama llama3.2.
     *
     * The prompt is structured to produce a well-formatted weekly workout
     * plan with day-by-day breakdown, sets/reps, and rugby context.
     */
    private String buildPrompt(WorkoutPlanRequest req) {
        String injuries = (req.getInjuryNotes() != null && !req.getInjuryNotes().isBlank())
                ? req.getInjuryNotes()
                : "None reported";

        return String.format("""
You are an expert rugby strength and conditioning coach specialising in university-level rugby (UTM Pirates, Malaysia).

Generate a detailed %d-day-per-week workout plan for a rugby player with the following profile:

PLAYER PROFILE:
- Position: %s
- Primary Goal: %s
- Training Level: %s
- Physical Stats: %d kg body weight, %d cm height, %d years old
- Sessions Per Week: %d
- Injury / Health Notes: %s

INSTRUCTIONS:
1. Structure the plan as exactly %d training days (e.g. Day 1, Day 2, ...) with rest days noted.
2. Each training day must include:
   a) Session Focus (e.g. "Lower Body Strength & Power")
   b) Warm-Up (5–10 minutes, specific movements)
   c) Main Workout (exercises with sets × reps, load guidance as %% of bodyweight or RPE, rest periods)
   d) Finisher / Conditioning (optional, position-appropriate)
   e) Cool-Down (5 minutes)
   f) Coaching Notes (position-specific tips relevant to %s)
3. Take the player's goal (%s) and training level (%s) into account when selecting exercises and volume.
4. IMPORTANT — Injury consideration: %s. Avoid exercises that aggravate these issues and include safe alternatives.
5. Use rugby-relevant exercises (scrummaging drills, tackle bag work, sprint intervals, etc.) where appropriate.
6. Format your output using clear markdown: use ## for day headings, ### for sub-sections, and bullet points for exercises.
7. End with a brief "Weekly Structure Summary" table showing Day → Focus → Volume.

Begin the plan now:
""",
                req.getSessionsPerWeek(),
                req.getRugbyPosition(),
                req.getGoal(),
                req.getTrainingLevel(),
                req.getWeight(),
                req.getHeight(),
                req.getAge(),
                req.getSessionsPerWeek(),
                injuries,
                req.getSessionsPerWeek(),
                req.getRugbyPosition(),
                req.getGoal(),
                req.getTrainingLevel(),
                injuries
        );
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private WorkoutPlanResponse toResponse(WorkoutPlan plan) {
        return WorkoutPlanResponse.builder()
                .id(plan.getId())
                .userId(plan.getUserId())
                .planName(plan.getPlanName())
                .rugbyPosition(plan.getRugbyPosition())
                .goal(plan.getGoal())
                .trainingLevel(plan.getTrainingLevel())
                .weight(plan.getWeight())
                .height(plan.getHeight())
                .age(plan.getAge())
                .injuryNotes(plan.getInjuryNotes())
                .sessionsPerWeek(plan.getSessionsPerWeek())
                .generatedPlan(plan.getGeneratedPlan())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
