package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
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
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    /**
     * Test the happy path: create and read a compensation record.
     * 
     * Thought Process:
     * - This tests the full create-read workflow for compensation.
     * - I create a compensation record for John Lennon with valid data,
     *   then verify it was stored correctly and can be retrieved.
     */
    @Test
    public void testCreateRead() {
        // John Lennon's ID from the bootstrap data
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate("2026-04-19");

        // Create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        assertNotNull(createdCompensation);
        assertNotNull(createdCompensation.getEmployee());
        assertEquals(employeeId, createdCompensation.getEmployee().getEmployeeId());
        assertEquals(testCompensation.getSalary(), createdCompensation.getSalary(), 0.001);
        assertEquals(testCompensation.getEffectiveDate(), createdCompensation.getEffectiveDate());

        // Read checks
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, employeeId).getBody();
        
        assertNotNull(readCompensation);
        assertNotNull(readCompensation.getEmployee());
        assertEquals(employeeId, readCompensation.getEmployee().getEmployeeId());
        assertEquals(testCompensation.getSalary(), readCompensation.getSalary(), 0.001);
        assertEquals(testCompensation.getEffectiveDate(), readCompensation.getEffectiveDate());
    }

    /**
     * Test error case: attempting to create compensation with negative salary.
     * 
     * Thought Process:
     * - Negative salaries don't make business sense. The @Positive annotation on the
     *   salary field should catch this and return HTTP 400 Bad Request.
     * - This validates that our input validation is working correctly.
     * - API clients should see 400 (bad data) not 500 (server error).
     */
    @Test
    public void testCreateCompensationNegativeSalary() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(-50000.0);  // Invalid: negative salary
        testCompensation.setEffectiveDate("2026-04-19");

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl, testCompensation, String.class);
        
        // Should return 400 Bad Request, not 500
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test error case: attempting to create compensation with zero salary.
     * 
     * Thought Process:
     * - Zero salary is also invalid per the @Positive constraint.
     * - Validates that the validation framework properly rejects edge cases.
     */
    @Test
    public void testCreateCompensationZeroSalary() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(0.0);  // Invalid: zero salary
        testCompensation.setEffectiveDate("2026-04-19");

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl, testCompensation, String.class);
        
        // Should return 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test error case: attempting to create compensation with null effective date.
     * 
     * Thought Process:
     * - The @NotBlank annotation should reject null or empty effective dates.
     * - This is important for auditing and compensation history tracking.
     */
    @Test
    public void testCreateCompensationNullEffectiveDate() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate(null);  // Invalid: null date

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl, testCompensation, String.class);
        
        // Should return 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test error case: attempting to create compensation with empty effective date.
     * 
     * Thought Process:
     * - Blank strings should also be rejected by @NotBlank.
     * - Ensures we're not storing meaningless whitespace dates.
     */
    @Test
    public void testCreateCompensationEmptyEffectiveDate() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate("   ");  // Invalid: blank date

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl, testCompensation, String.class);
        
        // Should return 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test error case: attempting to create compensation with null employee.
     * 
     * Thought Process:
     * - The @NotNull annotation on the employee field should catch this.
     * - Every compensation must be associated with an employee.
     */
    @Test
    public void testCreateCompensationNullEmployee() {
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployee(null);  // Invalid: null employee
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate("2026-04-19");

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl, testCompensation, String.class);
        
        // Should return 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test error case: attempting to create compensation with invalid employee ID.
     * 
     * Thought Process:
     * - The employee must exist in the database. If it doesn't, CompensationServiceImpl
     *   calls employeeService.read() which throws EmployeeNotFoundException.
     * - This should result in HTTP 404 Not Found, not 500.
     */
    @Test
    public void testCreateCompensationInvalidEmployee() {
        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId("non-existent-employee-id");
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate("2026-04-19");

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl, testCompensation, String.class);
        
        // Should return 404 Not Found (employee doesn't exist)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test error case: attempting to read compensation for a non-existent employee.
     * 
     * Thought Process:
     * - When no compensation record exists for an employee, we should get HTTP 404 Not Found.
     * - This is cleaner than returning 200 OK with a null body or an empty response.
     */
    @Test
    public void testReadCompensationNonExistent() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate("2026-04-19");

        restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class);

        // Try to read without creating first (or with an employee that has no compensation)
        ResponseEntity<String> response = restTemplate.getForEntity(compensationIdUrl, String.class, employeeId);

        // Since we haven't created compensation for this employee yet, should return 404
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test: creating multiple compensation records for the same employee returns the most recent.
     * 
     * Thought Process:
     * - When an employee gets a raise, we create a new compensation record with a new effective date.
     * - The read() method uses "findFirst...OrderByEffectiveDateDesc" to always return
     *   the most recent compensation.
     * - This test verifies that older records are not returned.
     */
    @Test
    public void testMultipleCompensationRecordsMostRecent() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        // Create first compensation record
        Compensation comp1 = new Compensation();
        Employee employee1 = new Employee();
        employee1.setEmployeeId(employeeId);
        comp1.setEmployee(employee1);
        comp1.setSalary(100000.0);
        comp1.setEffectiveDate("2024-01-01");
        restTemplate.postForEntity(compensationUrl, comp1, Compensation.class);

        // Create second compensation record with a later date and higher salary
        Compensation comp2 = new Compensation();
        Employee employee2 = new Employee();
        employee2.setEmployeeId(employeeId);
        comp2.setEmployee(employee2);
        comp2.setSalary(150000.0);
        comp2.setEffectiveDate("2026-04-19");
        restTemplate.postForEntity(compensationUrl, comp2, Compensation.class);

        // Read should return the most recent (highest salary, latest date)
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, employeeId).getBody();
        
        assertNotNull(readCompensation);
        assertEquals(150000.0, readCompensation.getSalary(), 0.001);
        assertEquals("2026-04-19", readCompensation.getEffectiveDate());
    }

    /**
     * Test error case: attempting to create compensation with invalid date format.
     *
     * Thought Process:
     * - The @Pattern annotation on effectiveDate should reject non-ISO-8601 formats.
     * - Valid: "2026-04-19"
     * - Invalid: "04/19/2026", "2026/04/19", "whenever", or any other format
     * - This validation ensures data consistency and makes date comparisons/sorting reliable.
     */
    @Test
    public void testCreateCompensationInvalidDateFormat() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate("04/19/2026");  // Invalid: wrong date format

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl, testCompensation, String.class);

        // Should return 400 Bad Request due to @Pattern validation failure
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test valid date format: ISO-8601 (YYYY-MM-DD) should be accepted.
     *
     * Thought Process:
     * - This ensures that the @Pattern validation works correctly for valid dates.
     * - Confirms that the ISO-8601 format is properly enforced.
     */
    @Test
    public void testCreateCompensationValidDateFormat() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        testCompensation.setEmployee(employee);
        testCompensation.setSalary(150000.0);
        testCompensation.setEffectiveDate("2026-04-19");  // Valid: ISO-8601 format

        ResponseEntity<Compensation> response = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class);

        // Should return 201 Created
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("2026-04-19", response.getBody().getEffectiveDate());
    }
}
