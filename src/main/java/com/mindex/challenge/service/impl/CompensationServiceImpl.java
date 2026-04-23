package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.exception.CompensationNotFoundException;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.exception.InvalidCompensationException;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    @Transactional
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        // Enhanced validation: I added explicit null checks to catch issues early
        // and provide meaningful error messages. This is important because catching
        // problems at the service layer means the API can return HTTP 400 Bad Request
        // instead of letting the error propagate deeper and potentially returning 500.
        if (compensation == null) {
            LOG.error("Compensation object cannot be null");
            throw new InvalidCompensationException("Compensation object cannot be null");
        }

        if (compensation.getEmployee() == null || compensation.getEmployee().getEmployeeId() == null) {
            LOG.error("Compensation must have an associated employee with a valid employeeId");
            throw new InvalidCompensationException("Compensation must have an associated employee with a valid employeeId");
        }

        // Fetch the fully populated Employee object from the database using the provided ID.
        // Thought Process:
        // - The client only needs to pass in {"employee": {"employeeId": "123..."}, ...}
        // - We use EmployeeService to validate the employee exists and fetch their full details
        //   before persisting. This guarantees data integrity and ensures the Compensation
        //   record has the complete Employee data embedded when we save/return it.
        // - If the employee doesn't exist, EmployeeService will throw EmployeeNotFoundException,
        //   which the controller will convert to HTTP 404.
        //
        // @Transactional Enhancement:
        // - This annotation ensures that both the read() and insert() operations happen atomically.
        // - If the employee is deleted between read() and insert(), the entire transaction rolls back
        //   instead of saving compensation for a non-existent employee.
        // - This prevents data integrity issues in concurrent scenarios.
        Employee employee = employeeService.read(compensation.getEmployee().getEmployeeId());
        compensation.setEmployee(employee);

        LOG.info("Compensation created successfully for employee [{}]", employee.getEmployeeId());
        return compensationRepository.insert(compensation);
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for employee with id [{}]", employeeId);
        
        // Added validation for input parameter. I always validate employeeId early
        // to avoid making database calls with invalid input.
        if (employeeId == null || employeeId.trim().isEmpty()) {
            LOG.error("Employee ID cannot be null or empty");
            throw new InvalidCompensationException("Employee ID cannot be null or empty");
        }
        
        Compensation compensation = compensationRepository.findFirstByEmployee_EmployeeIdOrderByEffectiveDateDesc(employeeId);

        // Enhancement: Replaced generic RuntimeException with a custom CompensationNotFoundException.
        // This allows the controller to catch this specific exception and return an appropriate
        // HTTP 404 Not Found response. It also makes debugging easier since the exception type
        // clearly indicates what went wrong.
        if (compensation == null) {
            LOG.warn("No compensation found for employeeId: {}", employeeId);
            throw new CompensationNotFoundException("No compensation found for employeeId: " + employeeId);
        }

        LOG.info("Compensation retrieved successfully for employee [{}]", employeeId);
        return compensation;
    }
}
