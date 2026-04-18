package com.utm.rugbyplanner.repository;

import com.utm.rugbyplanner.model.MealPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends MongoRepository<MealPlan, String> {

    /** All plans belonging to a user, newest first */
    List<MealPlan> findByUserIdOrderByCreatedAtDesc(String userId);

    /** Find a specific plan owned by a user (for security checks) */
    Optional<MealPlan> findByIdAndUserId(String id, String userId);

    /** Count plans for a user */
    long countByUserId(String userId);
}
