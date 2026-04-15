package com.utm.rugbyplanner.dto;

import com.utm.rugbyplanner.model.User;
import lombok.*;

/** UC001 Login — response body (JWT + user info for Vue state) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private String        userId;
    private String        username;
    private String        fullName;
    private String        email;
    private User.UserRole userRole;
}
