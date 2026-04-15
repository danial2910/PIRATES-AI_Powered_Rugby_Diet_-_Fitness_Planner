package com.utm.rugbyplanner.dto;

import com.utm.rugbyplanner.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;

/** UC002 Register — request body */
@Data
public class RegisterRequest {

    // Step 3: name + email
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Pattern(
        regexp = "^(\\+?[0-9]{10,15})?$",
        message = "Please enter a valid phone number"
    )
    private String phoneNumber;

    // Step 4: username + password
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Username can only contain letters, numbers, and underscores"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    // Step 5: role selection
    @NotNull(message = "Please select a role")
    private User.UserRole userRole;
}
