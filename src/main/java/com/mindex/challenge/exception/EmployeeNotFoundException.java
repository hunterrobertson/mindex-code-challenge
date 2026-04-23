package com.mindex.challenge.exception;

/**
 * Custom exception thrown when an employee cannot be found in the database.
 * 
 * Thought Process:
 * - Instead of throwing generic RuntimeExceptions, I created a custom exception
 *   to make error handling more explicit and maintainable.
 * - This allows the controller to catch specific exceptions and return appropriate
 *   HTTP status codes (e.g., 404 Not Found).
 * - Improves code clarity and follows Spring Boot best practices for exception handling.
 */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }

    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
