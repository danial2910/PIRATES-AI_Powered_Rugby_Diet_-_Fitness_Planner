package com.utm.rugbyplanner.exception;

/** UC002 AF1 — custom validation failure e.g. password mismatch (→ 400) */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
