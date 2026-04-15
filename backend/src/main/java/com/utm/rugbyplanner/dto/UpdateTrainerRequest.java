package com.utm.rugbyplanner.dto;

import com.utm.rugbyplanner.model.Trainer;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * UC003: Update Profile — Trainer-specific fields.
 *
 * availabilitySlots  → list of { day, startTime, endTime }
 * certificationFiles → list of { name, fileName, fileUrl, uploadedAt }
 * expertise          → free text
 * experience         → free text
 */
@Data
public class UpdateTrainerRequest {

    /** Weekly schedule slots — replaces plain String availability */
    private List<Trainer.AvailabilitySlot> availabilitySlots;

    @Size(max = 300, message = "Expertise cannot exceed 300 characters")
    private String expertise;

    @Size(max = 500, message = "Experience cannot exceed 500 characters")
    private String experience;

    /** Certification files — name + base64 data or URL */
    private List<Trainer.CertificationFile> certificationFiles;
}