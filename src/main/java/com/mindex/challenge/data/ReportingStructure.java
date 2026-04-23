package com.mindex.challenge.data;

/**
 * Task 1: Create a new type called ReportingStructure.
 * This class holds the fully filled out Employee and the total number of their reports.
 * 
 * Thought Process:
 * - We need to encapsulate an Employee object and an integer representing the total count
 *   of all their direct and indirect reports.
 * - This acts as a Data Transfer Object (DTO) to return the expected JSON structure
 *   for the /reportingStructure endpoint.
 */
public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    public ReportingStructure() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}
