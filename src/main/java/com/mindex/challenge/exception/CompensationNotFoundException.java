package com.mindex.challenge.exception;

/**
 * Custom exception thrown when compensation data cannot be found for an employee.
 * 
 * Thought Process:
 * - This exception is more semantically appropriate than a generic RuntimeException
 *   for compensation-specific lookup failures.
 * - Allows controllers to handle compensation-not-found scenarios distinctly
 *   from other error types, improving API error responses.
 * - Makes the code more maintainable and easier to debug.
 */
public class CompensationNotFoundException extends RuntimeException {
    public CompensationNotFoundException(String message) {
        super(message);
    }

    public CompensationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
