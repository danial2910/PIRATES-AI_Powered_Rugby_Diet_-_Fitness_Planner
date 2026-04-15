package com.utm.rugbyplanner.repository;

import com.utm.rugbyplanner.model.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends MongoRepository<Trainer, String> {

    Optional<Trainer> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
