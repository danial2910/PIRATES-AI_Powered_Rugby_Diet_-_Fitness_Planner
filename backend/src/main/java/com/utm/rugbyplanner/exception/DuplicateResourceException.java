package com.utm.rugbyplanner.exception;

/** UC002 AF2 — duplicate email or username (→ 409 Conflict) */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
