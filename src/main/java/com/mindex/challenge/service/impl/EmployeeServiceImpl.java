package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        // Enhancement: Replaced generic RuntimeException with custom EmployeeNotFoundException.
        // This makes error handling more explicit and allows controllers to return appropriate
        // HTTP status codes. Instead of a vague RuntimeException (500 error), this specifically
        // indicates a resource was not found (404 error).
        if (employee == null) {
            LOG.warn("Employee not found with id: {}", id);
            throw new EmployeeNotFoundException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    @Transactional
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);
        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        LOG.info("Starting to create reporting structure for employee with id [{}]", id);
        Employee employee = read(id);
        LOG.info("Successfully read employee with id [{}]", id);
        int numberOfReports = countReports(employee);
        LOG.info("Successfully counted [{}] reports for employee with id [{}]", numberOfReports, id);
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(numberOfReports);
        LOG.info("Successfully created reporting structure for employee with id [{}]", id);
        return reportingStructure;
    }

    private int countReports(Employee employee) {
        if (employee.getDirectReports() == null) {
            LOG.debug("Employee with id [{}] has no direct reports.", employee.getEmployeeId());
            return 0;
        }

        int count = 0;
        LOG.debug("Starting to count reports for employee with id [{}]", employee.getEmployeeId());

        List<Employee> reportsToProcess = new ArrayList<>();
        for (Employee directReport : employee.getDirectReports()) {
            reportsToProcess.add(read(directReport.getEmployeeId()));
        }

        Set<String> processedEmployeeIds = new HashSet<>();

        while (!reportsToProcess.isEmpty()) {
            Employee currentReport = reportsToProcess.remove(0);
            if (processedEmployeeIds.add(currentReport.getEmployeeId())) {
                count++;
                if (currentReport.getDirectReports() != null) {
                    for (Employee report : currentReport.getDirectReports()) {
                        reportsToProcess.add(read(report.getEmployeeId()));
                    }
                }
            }
        }
        return count;
    }
}
