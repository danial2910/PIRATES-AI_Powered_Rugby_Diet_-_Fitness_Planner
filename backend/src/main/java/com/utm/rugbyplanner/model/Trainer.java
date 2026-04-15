package com.utm.rugbyplanner.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Trainer — MongoDB collection: "trainers"
 *
 * availability  → List of AvailabilitySlot (day + startTime + endTime)
 * certifications → List of CertificationFile (name + fileUrl + uploadedAt)
 * expertise      → free text
 * experience     → free text
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
    private String userId;

    /** Structured weekly schedule — replaces plain String availability */
    private List<AvailabilitySlot> availabilitySlots;

    private String expertise;
    private String experience;

    /** Uploaded certification files */
    private List<CertificationFile> certificationFiles;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // ── Embedded documents ────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilitySlot {
        private String day;        // "MONDAY", "TUESDAY" … "SUNDAY"
        private String startTime;  // "08:00"
        private String endTime;    // "17:00"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationFile {
        private String name;        // e.g. "World Rugby Level 2"
        private String fileName;    // original filename
        private String fileUrl;     // stored path / base64 data URL
        private String uploadedAt;  // ISO timestamp string
    }
}