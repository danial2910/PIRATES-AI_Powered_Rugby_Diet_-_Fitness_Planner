package com.utm.rugbyplanner.service;

import com.utm.rugbyplanner.dto.*;
import com.utm.rugbyplanner.exception.DuplicateResourceException;
import com.utm.rugbyplanner.model.Athlete;
import com.utm.rugbyplanner.model.Trainer;
import com.utm.rugbyplanner.model.User;
import com.utm.rugbyplanner.repository.AthleteRepository;
import com.utm.rugbyplanner.repository.TrainerRepository;
import com.utm.rugbyplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository    userRepository;
    private final AthleteRepository athleteRepository;
    private final TrainerRepository trainerRepository;

    // ── Get profile ───────────────────────────────────────────────────────

    public ProfileResponse getProfile(String username) {
        User user = findUser(username);

        ProfileResponse.ProfileResponseBuilder b = ProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .userRole(user.getUserRole());

        if (user.getUserRole() == User.UserRole.ATHLETE) {
            athleteRepository.findByUserId(user.getId()).ifPresent(a -> b
                    .athleteId(a.getId())
                    .weight(a.getWeight())
                    .targetWeight(a.getTargetWeight())
                    .height(a.getHeight())
                    .age(a.getAge())
                    .location(a.getLocation())
                    .goal(a.getGoal())
                    .activityLevel(a.getActivityLevel())
                    .rugbyPosition(a.getRugbyPosition())
                    .dietaryRestrictions(a.getDietaryRestrictions())
                    .injuryNotes(a.getInjuryNotes())
                    .trainingLevel(a.getTrainingLevel())
            );
        } else {
            trainerRepository.findByUserId(user.getId()).ifPresent(t -> b
                    .trainerId(t.getId())
                    .availabilitySlots(t.getAvailabilitySlots())
                    .expertise(t.getExpertise())
                    .experience(t.getExperience())
                    .certificationFiles(t.getCertificationFiles())
            );
        }

        return b.build();
    }

    // ── Update user info (personal info + profile picture) ────────────────

    @Transactional
    public ProfileResponse updateUserInfo(String username, UpdateUserRequest req) {
        User user = findUser(username);

        // Check new email not taken by another user
        if (!user.getEmail().equalsIgnoreCase(req.getEmail()) &&
                userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException(
                    "This email address is already registered by another account.");
        }

        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());

        // Update profile picture — null means keep existing, empty string means remove
        if (req.getProfilePicture() != null) {
            if (req.getProfilePicture().isEmpty()) {
                user.setProfilePicture(null);  // remove picture
            } else {
                user.setProfilePicture(req.getProfilePicture());  // set new picture
            }
        }
        // if req.getProfilePicture() == null → no change (don't overwrite)

        userRepository.save(user);
        log.info("UC003 user info updated — username: {}", username);
        return getProfile(username);
    }

    // ── Update athlete profile ────────────────────────────────────────────

    @Transactional
    public ProfileResponse updateAthleteProfile(String username, UpdateAthleteRequest req) {
        User user = findUser(username);

        Athlete athlete = athleteRepository.findByUserId(user.getId())
                .orElseGet(() -> Athlete.builder().userId(user.getId()).build());

        athlete.setWeight(req.getWeight());
        athlete.setTargetWeight(req.getTargetWeight());
        athlete.setHeight(req.getHeight());
        athlete.setAge(req.getAge());
        athlete.setLocation(req.getLocation());
        athlete.setGoal(req.getGoal());
        athlete.setActivityLevel(req.getActivityLevel());
        athlete.setRugbyPosition(req.getRugbyPosition());
        athlete.setDietaryRestrictions(req.getDietaryRestrictions());
        athlete.setInjuryNotes(req.getInjuryNotes());
        athlete.setTrainingLevel(req.getTrainingLevel());

        athleteRepository.save(athlete);
        log.info("UC003 athlete profile updated — username: {}", username);
        return getProfile(username);
    }

    // ── Update trainer profile ────────────────────────────────────────────

    @Transactional
    public ProfileResponse updateTrainerProfile(String username, UpdateTrainerRequest req) {
        User user = findUser(username);

        Trainer trainer = trainerRepository.findByUserId(user.getId())
                .orElseGet(() -> Trainer.builder().userId(user.getId()).build());

        trainer.setAvailabilitySlots(req.getAvailabilitySlots());
        trainer.setExpertise(req.getExpertise());
        trainer.setExperience(req.getExperience());
        trainer.setCertificationFiles(req.getCertificationFiles());

        trainerRepository.save(trainer);
        log.info("UC003 trainer profile updated — username: {}", username);
        return getProfile(username);
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));
    }
}