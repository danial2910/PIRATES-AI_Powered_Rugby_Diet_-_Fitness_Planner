package com.utm.rugbyplanner.repository;

import com.utm.rugbyplanner.model.WorkoutPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutPlanRepository extends MongoRepository<WorkoutPlan, String> {

    /** All plans belonging to a user, newest first */
    List<WorkoutPlan> findByUserIdOrderByCreatedAtDesc(String userId);

    /** Find a specific plan owned by a user (for security checks) */
    Optional<WorkoutPlan> findByIdAndUserId(String id, String userId);

    /** Count plans for a user */
    long countByUserId(String userId);
}
