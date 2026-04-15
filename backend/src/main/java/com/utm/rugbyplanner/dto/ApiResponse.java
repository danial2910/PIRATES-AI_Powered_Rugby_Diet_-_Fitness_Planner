package com.utm.rugbyplanner.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Generic API response wrapper.
 * Every endpoint returns this — ensures consistent shape for Vue axios handling.
 *
 * Success:  { "success": true,  "message": "...", "data": { … } }
 * Error:    { "success": false, "message": "..." }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String  message;
    private T       data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
