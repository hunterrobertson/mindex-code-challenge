package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reportingStructure";
    }

    /**
     * Test the happy path: fetching the reporting structure for an employee with reports.
     * 
     * Thought Process:
     * - This is the main use case from the requirements. John Lennon has 4 total reports.
     * - I validate that the returned ReportingStructure contains the correct employee
     *   and the correct count of reports.
     */
    @Test
    public void testGenerateReportingStructure() {
        // John Lennon's ID from the bootstrap data
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        // Read checks
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employeeId).getBody();
        
        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(employeeId, reportingStructure.getEmployee().getEmployeeId());
        
        // John Lennon has 4 total reports in the bootstrap data
        assertEquals(4, reportingStructure.getNumberOfReports());
    }

    /**
     * Test edge case: fetching reporting structure for an employee with no direct reports.
     * 
     * Thought Process:
     * - George Harrison (Pete Best's sibling) has no direct reports.
     * - The numberOfReports should be 0, not null or undefined.
     * - This ensures the endpoint handles leaf nodes in the hierarchy correctly.
     */
    @Test
    public void testGenerateReportingStructureWithNoReports() {
        // George Harrison's ID - an employee with no direct reports
        String employeeId = "3aa1cbe4-8801-11e8-9eb6-529269fb1459";

        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employeeId).getBody();
        
        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(employeeId, reportingStructure.getEmployee().getEmployeeId());
        
        // George has 0 reports
        assertEquals(0, reportingStructure.getNumberOfReports());
    }

    /**
     * Test error case: requesting reporting structure for a non-existent employee.
     * 
     * Thought Process:
     * - When querying for an invalid employee ID, the endpoint should return HTTP 404 Not Found.
     * - NOT HTTP 500 Internal Server Error.
     * - This is important because it tells API clients the resource doesn't exist, not that
     *   something broke on the server.
     */
    @Test
    public void testGenerateReportingStructureInvalidEmployee() {
        String invalidEmployeeId = "invalid-employee-id-that-does-not-exist";

        ResponseEntity<ReportingStructure> response = restTemplate.getForEntity(
            reportingStructureUrl, 
            ReportingStructure.class, 
            invalidEmployeeId
        );
        
        // Should return 404, not 500
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test employee with one level of direct reports.
     * 
     * Thought Process:
     * - Ringo Starr is the middle manager: he has 2 direct reports (Pete Best and George Harrison)
     *   but no reports beyond that.
     * - numberOfReports should be 2, not counting Ringo himself.
     */
    @Test
    public void testGenerateReportingStructureSingleLevel() {
        // Ringo Starr's ID - has direct reports but they have no reports
        String employeeId = "03aa1cbe-8801-11e8-9eb6-529269fb1459";

        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employeeId).getBody();
        
        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(employeeId, reportingStructure.getEmployee().getEmployeeId());
        
        // Ringo has 2 direct reports
        assertEquals(2, reportingStructure.getNumberOfReports());
    }
}
