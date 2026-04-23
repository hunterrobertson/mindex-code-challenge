package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportingStructureController {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureController.class);

    @Autowired
    private ReportingStructureService reportingStructureService;

    /**
     * Endpoint to fetch the fully filled out ReportingStructure for a given employeeId.
     * The values are computed on the fly by the service layer.
     * 
     * Thought Process:
     * - We need a GET endpoint to retrieve the reporting structure.
     * - Using the URL mapping "/employee/{id}/reportingStructure" clearly indicates 
     *   that reporting structure is a sub-resource or computed view of a specific employee.
     * - I delegate the computation to the ReportingStructureService to keep the 
     *   controller lean and focused strictly on handling the HTTP request/response.
     * 
     * Error handling:
     * - If the employee doesn't exist, the service will throw EmployeeNotFoundException.
     * - GlobalExceptionHandler will catch this and return HTTP 404 Not Found.
     * - No try-catch needed here - clean separation of concerns.
     */
    @GetMapping("/employee/{id}/reportingStructure")
    public ReportingStructure read(@PathVariable String id) {
        LOG.debug("Received reporting structure read request for id [{}]", id);

        return reportingStructureService.generateReportingStructure(id);
    }
}
