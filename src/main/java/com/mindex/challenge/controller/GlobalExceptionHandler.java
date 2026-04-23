package com.mindex.challenge.controller;

import com.mindex.challenge.exception.CompensationNotFoundException;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.exception.InvalidCompensationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * 
 * Thought Process:
 * - I created this centralized exception handler to manage error responses consistently
 *   across all controllers (EmployeeController, CompensationController, ReportingStructureController).
 * - Instead of having try-catch blocks scattered throughout each controller, this handler
 *   intercepts exceptions and converts them to appropriate HTTP responses.
 * - Benefits:
 *   1. Consistent error response format across the entire API
 *   2. Easier to maintain - changes to error responses only need to be made in one place
 *   3. Cleaner controllers - they can focus on business logic instead of error handling
 *   4. Proper HTTP status codes: 404 for not found, 400 for bad input, 500 for server errors
 *   5. Meaningful error messages that help API clients understand what went wrong
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles EmployeeNotFoundException and returns HTTP 404 Not Found.
     * 
     * Why this is important:
     * - When an employee is not found, returning 404 is semantically correct and follows REST conventions.
     * - Previously, this would have thrown a generic RuntimeException and returned 500 Internal Server Error.
     * - Now API clients know the issue is a missing resource, not a server problem.
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmployeeNotFound(EmployeeNotFoundException e) {
        LOG.error("Employee not found: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Employee Not Found");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles CompensationNotFoundException and returns HTTP 404 Not Found.
     * 
     * Why this matters:
     * - Compensation records might not exist for every employee, especially for new hires.
     * - A 404 tells the client "we couldn't find what you asked for," which is different from
     *   a 500 error that says "something broke on our end."
     * - This distinction is crucial for robust API clients.
     */
    @ExceptionHandler(CompensationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCompensationNotFound(CompensationNotFoundException e) {
        LOG.error("Compensation not found: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Compensation Not Found");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InvalidCompensationException and returns HTTP 400 Bad Request.
     * 
     * Why 400 instead of 500:
     * - 400 Bad Request means the client sent invalid data (null salary, missing employee, etc.).
     * - 500 Internal Server Error means something broke on our side.
     * - This tells clients they need to fix their request, not that our server is broken.
     * - Improves developer experience when consuming the API.
     */
    @ExceptionHandler(InvalidCompensationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCompensation(InvalidCompensationException e) {
        LOG.error("Invalid compensation data: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid Compensation");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors from @Valid annotations.
     * 
     * Why this is useful:
     * - When @Valid fails on a Compensation object (e.g., salary is negative or employee is null),
     *   Spring throws MethodArgumentNotValidException.
     * - This handler catches that and returns a clean 400 error with field-level details.
     * - Clients can see exactly which fields failed validation and why.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(MethodArgumentNotValidException e) {
        LOG.error("Validation error: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation Failed");
        error.put("message", e.getBindingResult().getFieldError() != null 
            ? e.getBindingResult().getFieldError().getDefaultMessage()
            : "Invalid request body");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches any other RuntimeExceptions that aren't handled above.
     * 
     * Why we need this fallback:
     * - Acts as a safety net for unexpected errors.
     * - Returns a generic 500 Internal Server Error.
     * - Ensures no stacktraces are exposed to the client (security best practice).
     * - Logs the full error for debugging on the server side.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        LOG.error("Unexpected error: ", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
