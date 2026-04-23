package com.mindex.challenge.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Task 2: Create a new type called Compensation.
 * It contains an Employee, their salary, and the effective date of the compensation.
 * 
 * Thought Process:
 * - The requirements state it needs at least `salary` and `effectiveDate`, and 
 *   to be associated with a specific `Employee`.
 * - Storing the full `Employee` object here instead of just an ID ensures that when
 *   we fetch a Compensation record, we have all the context we need to send back
 *   to the client without forcing them to make a second API call.
 * 
 * Enhancement - Added validation annotations:
 * - @NotNull on employee: Ensures a Compensation must be tied to an employee.
 * - @Positive on salary: Validates that salary is a positive number (no negative/zero salaries).
 * - @NotBlank on effectiveDate: Ensures the effective date is provided and not empty.
 * - @Pattern on effectiveDate: Validates ISO-8601 format (YYYY-MM-DD). Prevents invalid
 *   dates like "whenever" or "2026/04/19" (wrong format). This ensures data consistency
 *   and makes comparisons/sorting reliable.
 * - These annotations work with Spring's validation framework to automatically
 *   catch invalid inputs and return HTTP 400 Bad Request responses with details
 *   about what fields failed validation. This is much better than silently accepting
 *   bad data or catching it deeper in the service layer.
 *
 * Future Enhancement Ideas (not in scope):
 * - Add bonusAmount, stockOptionsCount, signingBonus for richer compensation models
 * - Add currency field (to support international teams)
 * - Add compensationChangeReason for audit trails ('promotion', 'market adjustment', etc.)
 * - Use @FutureOrPresent if we want to only allow current or future dates
 * - Add @Min/@Max on salary if company has policy constraints
 */
public class Compensation {
    @NotNull(message = "Employee cannot be null")
    private Employee employee;
    
    @Positive(message = "Salary must be a positive value")
    private double salary;

    @NotBlank(message = "Effective date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Effective date must be in ISO-8601 format (YYYY-MM-DD)")
    private String effectiveDate;

    public Compensation() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
