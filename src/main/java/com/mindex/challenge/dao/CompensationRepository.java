package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {
    
    /**
     * Thought Process:
     * - We need to query Compensation documents by the nested employee.employeeId field.
     * - Spring Data MongoDB allows querying nested properties using the underscore '_' 
     *   to safely navigate object graphs (e.g., Employee_EmployeeId maps to compensation.employee.employeeId).
     * - Using "findFirst...OrderByEffectiveDateDesc" ensures that if an employee has 
     *   multiple compensation records over time, we return the most recent one.
     */
    Compensation findFirstByEmployee_EmployeeIdOrderByEffectiveDateDesc(String employeeId);
}
