package com.utm.rugbyplanner.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * User — MongoDB collection: "users"
 *
 * Central authentication document. Every person who accesses the
 * system (athlete or trainer) has exactly one User record.
 *
 * SDD Entity fields:
 *   userId    → MongoDB @Id (auto-generated ObjectId)
 *   userName  → unique username used at login (UC001)
 *   email     → unique email address
 *   password  → BCrypt-hashed password
 *   fullName  → display name shown on dashboard
 *   userRole  → ATHLETE | TRAINER  (from UC002 role selection)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;      // BCrypt hash — never stored plain

    private String fullName;

    private String phoneNumber;

    private UserRole userRole;    // ATHLETE | TRAINER

    @Builder.Default
    private boolean enabled = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum UserRole {
        ATHLETE,
        TRAINER
    }
}
