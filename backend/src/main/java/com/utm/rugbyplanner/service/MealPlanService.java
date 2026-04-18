package com.utm.rugbyplanner.service;

import com.utm.rugbyplanner.dto.MealPlanRequest;
import com.utm.rugbyplanner.dto.MealPlanResponse;
import com.utm.rugbyplanner.model.MealPlan;
import com.utm.rugbyplanner.model.User;
import com.utm.rugbyplanner.repository.MealPlanRepository;
import com.utm.rugbyplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MealPlanService — UC006: Create Meal Plan / UC007: Manage Meal Plan
 *
 * Responsibilities:
 *   1. Build a rugby-specific 7-day meal prompt from the user's inputs.
 *   2. Estimate the user's TDEE based on weight, height, age and goal.
 *   3. Call OllamaService to generate the plan text.
 *   4. Persist the plan to MongoDB.
 *   5. Provide list / delete operations for UC007.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository     userRepository;
    private final OllamaService      ollamaService;

    // ── UC006: Generate a new meal plan ──────────────────────────────────────

    public MealPlanResponse generatePlan(String username, MealPlanRequest req) {
        User user = findUser(username);

        log.info("UC006 Generate meal plan — user: {}, position: {}, goal: {}, diet: {}",
                username, req.getRugbyPosition(), req.getGoal(), req.getDietaryPreference());

        String prompt = buildPrompt(req);
        String generatedPlan = ollamaService.generate(prompt);

        String planName = (req.getPlanName() != null && !req.getPlanName().isBlank())
                ? req.getPlanName()
                : req.getRugbyPosition() + " – " + goalLabel(req.getGoal()) + " Meal Plan";

        MealPlan plan = MealPlan.builder()
                .userId(user.getId())
                .planName(planName)
                .rugbyPosition(req.getRugbyPosition())
                .goal(req.getGoal())
                .weight(req.getWeight())
                .height(req.getHeight())
                .age(req.getAge())
                .dietaryPreference(req.getDietaryPreference())
                .allergies(req.getAllergies())
                .mealsPerDay(req.getMealsPerDay())
                .generatedPlan(generatedPlan)
                .build();

        MealPlan saved = mealPlanRepository.save(plan);
        log.info("UC006 Meal plan saved — id: {}", saved.getId());

        return toResponse(saved);
    }

    // ── UC007: List saved plans ───────────────────────────────────────────────

    public List<MealPlanResponse> getPlans(String username) {
        User user = findUser(username);
        return mealPlanRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── UC007: Get single plan ────────────────────────────────────────────────

    public MealPlanResponse getPlan(String username, String planId) {
        User user = findUser(username);
        MealPlan plan = mealPlanRepository
                .findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new RuntimeException("Meal plan not found."));
        return toResponse(plan);
    }

    // ── UC007: Delete a plan ──────────────────────────────────────────────────

    public void deletePlan(String username, String planId) {
        User user = findUser(username);
        MealPlan plan = mealPlanRepository
                .findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new RuntimeException("Meal plan not found."));
        mealPlanRepository.delete(plan);
        log.info("UC007 Meal plan deleted — id: {}", planId);
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(MealPlanRequest req) {
        String allergies = (req.getAllergies() != null && !req.getAllergies().isBlank())
                ? req.getAllergies() : "None";

        // Estimate daily calorie target based on goal and rough TDEE
        int tdee = estimateTdee(req.getWeight(), req.getHeight(), req.getAge());
        int targetCalories = adjustCaloriesForGoal(tdee, req.getGoal());

        return String.format("""
You are an expert sports nutritionist specialising in rugby performance nutrition for university athletes (UTM Pirates, Malaysia).

Generate a complete 7-day meal plan for a rugby player with the following profile:

PLAYER PROFILE:
- Position: %s
- Nutrition Goal: %s
- Physical Stats: %d kg, %d cm, %d years old
- Estimated Daily Calorie Target: ~%d kcal
- Dietary Preference: %s
- Food Allergies / Intolerances: %s
- Meals Per Day: %d

INSTRUCTIONS:
1. Create a structured plan for Day 1 (Monday) through Day 7 (Sunday).
2. Each day must include exactly %d meals labelled clearly (e.g. Breakfast, Morning Snack, Lunch, Afternoon Snack, Dinner, Evening Snack — use only as many as needed for %d meals).
3. For each meal provide:
   a) Meal name / description
   b) Specific foods with portion sizes (grams or standard Malaysian measures)
   c) Estimated macros: Protein (g), Carbs (g), Fat (g), Calories (kcal)
4. Strictly follow the dietary preference (%s) — if HALAL, all ingredients must be halal-certified; if VEGETARIAN, no meat/fish; if VEGAN, no animal products.
5. IMPORTANT — Allergy consideration: %s. Never include these ingredients or derivatives.
6. Use locally available Malaysian foods and ingredients where possible (e.g. nasi lemak, roti canai, ikan bakar, ayam goreng, mee goreng — adapted to meet nutritional goals).
7. Vary the meals across the 7 days — avoid repeating the same meal more than twice.
8. On Day 3 (match/training day), increase carbohydrate loading by 15-20%% for pre-match energy.
9. On Day 7 (recovery day), emphasise anti-inflammatory foods and higher protein for muscle repair.
10. End with a "Weekly Nutrition Summary" table: Day | Total Calories | Protein | Carbs | Fat.
11. Format output using markdown: ## for day headings, ### for meal names, bullet points for food items.

Begin the 7-day meal plan now:
""",
                req.getRugbyPosition(),
                goalLabel(req.getGoal()),
                req.getWeight(), req.getHeight(), req.getAge(),
                targetCalories,
                req.getDietaryPreference(),
                allergies,
                req.getMealsPerDay(),
                req.getMealsPerDay(), req.getMealsPerDay(),
                req.getDietaryPreference(),
                allergies
        );
    }

    // ── TDEE estimation (Mifflin-St Jeor + activity multiplier) ─────────────

    /**
     * Rough TDEE estimate for a rugby athlete (active training days assumed).
     * Uses Mifflin-St Jeor BMR × 1.725 (very active).
     */
    private int estimateTdee(int weight, int height, int age) {
        // Assume male (most rugby players) — BMR = 10W + 6.25H - 5A + 5
        double bmr = (10.0 * weight) + (6.25 * height) - (5.0 * age) + 5;
        return (int) Math.round(bmr * 1.725);
    }

    private int adjustCaloriesForGoal(int tdee, String goal) {
        return switch (goal) {
            case "MUSCLE_GAIN"  -> tdee + 400;   // caloric surplus
            case "WEIGHT_LOSS"  -> tdee - 400;   // caloric deficit
            case "PERFORMANCE"  -> tdee + 200;   // slight surplus for performance
            default             -> tdee;         // MAINTAIN
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String goalLabel(String goal) {
        return switch (goal) {
            case "MUSCLE_GAIN"  -> "Muscle Gain";
            case "WEIGHT_LOSS"  -> "Weight Loss";
            case "PERFORMANCE"  -> "Performance";
            default             -> "Maintenance";
        };
    }

    private MealPlanResponse toResponse(MealPlan plan) {
        return MealPlanResponse.builder()
                .id(plan.getId())
                .userId(plan.getUserId())
                .planName(plan.getPlanName())
                .rugbyPosition(plan.getRugbyPosition())
                .goal(plan.getGoal())
                .weight(plan.getWeight())
                .height(plan.getHeight())
                .age(plan.getAge())
                .dietaryPreference(plan.getDietaryPreference())
                .allergies(plan.getAllergies())
                .mealsPerDay(plan.getMealsPerDay())
                .generatedPlan(plan.getGeneratedPlan())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
