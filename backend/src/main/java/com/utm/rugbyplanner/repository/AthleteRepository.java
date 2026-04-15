package com.utm.rugbyplanner.repository;

import com.utm.rugbyplanner.model.Athlete;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AthleteRepository extends MongoRepository<Athlete, String> {

    Optional<Athlete> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
