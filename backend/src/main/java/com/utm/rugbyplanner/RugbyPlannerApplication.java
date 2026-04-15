package com.utm.rugbyplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * AI-Powered Rugby Diet & Fitness Planner
 * Universiti Teknologi Malaysia — Faculty of Computing
 *
 * @author Muhammad Danial Syafiq Bin Ermiza
 */
@SpringBootApplication
@EnableMongoAuditing   // enables @CreatedDate / @LastModifiedDate on MongoDB documents
public class RugbyPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RugbyPlannerApplication.class, args);
    }
}
