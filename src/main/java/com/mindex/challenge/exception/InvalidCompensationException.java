package com.mindex.challenge.exception;

/**
 * Custom exception thrown when compensation data fails validation.
 * 
 * Thought Process:
 * - This exception is raised when compensation input violates business rules
 *   (e.g., negative salary, invalid date format, missing fields).
 * - Allows controllers to return HTTP 400 Bad Request with meaningful messages.
 * - Separates validation failures from data lookup failures, improving API clarity.
 */
public class InvalidCompensationException extends RuntimeException {
    public InvalidCompensationException(String message) {
        super(message);
    }

    public InvalidCompensationException(String message, Throwable cause) {
        super(message, cause);
    }
}
