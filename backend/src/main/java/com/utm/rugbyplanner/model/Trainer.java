package com.utm.rugbyplanner.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Trainer — MongoDB collection: "trainers"
 *
 * Created automatically when a user registers with role = TRAINER.
 * Linked 1-to-1 with User via userId.
 *
 * SDD Entity fields: availability, expertise, experience, certifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainers")
public class Trainer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;          // FK → User._id

    private String availability;    // e.g. "Mon-Fri 8am-5pm"
    private String expertise;       // e.g. "Strength & Conditioning, Forwards"
    private String experience;      // e.g. "5 years UTM Pirates coach"
    private String certifications;  // e.g. "World Rugby Level 2"

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
