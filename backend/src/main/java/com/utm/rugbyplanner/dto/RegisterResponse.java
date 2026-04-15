package com.utm.rugbyplanner.dto;

import com.utm.rugbyplanner.model.User;
import lombok.*;

/** UC002 Register — response body */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private String        userId;
    private String        username;
    private String        fullName;
    private String        email;
    private User.UserRole userRole;
    private String        message;
}
