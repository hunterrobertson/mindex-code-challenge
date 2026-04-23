package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    /**
     * Endpoint to create a new Compensation record in the database.
     * Expects a JSON payload containing an employee (with at least the employeeId),
     * salary, and effectiveDate.
     * 
     * Thought Process:
     * - Uses a POST request as this creates a new record.
     * - Expects the entire Compensation object as the request body.
     * 
     * Enhancement - Added @Valid annotation:
     * - @Valid triggers Spring's validation framework to check the Compensation object
     *   against all the validation annotations I added to the Compensation class
     *   (@NotNull, @Positive, @NotBlank).
     * - If validation fails, Spring automatically throws MethodArgumentNotValidException,
     *   which GlobalExceptionHandler catches and converts to HTTP 400 Bad Request.
     * - This catches bad data at the controller boundary, preventing invalid data from
     *   reaching the service layer or database.
     */
    @PostMapping("/compensation")
    public Compensation create(@Valid @RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);

        return compensationService.create(compensation);
    }

    /**
     * Endpoint to read a Compensation record from the database.
     * Expects an employeeId in the URL.
     * 
     * Note on error handling:
     * - If the employee ID is invalid or compensation is not found,
     *   CompensationService will throw CompensationNotFoundException.
     * - GlobalExceptionHandler will catch this and return HTTP 404 Not Found.
     * - This is cleaner than having try-catch blocks in the controller.
     */
    @GetMapping("/compensation/{id}")
    public Compensation read(@PathVariable String id) {
        LOG.debug("Received compensation read request for id [{}]", id);

        return compensationService.read(id);
    }
}
