package com.interviewprep.ems.repository;

import com.interviewprep.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    // Demonstrates JPQL + JOIN FETCH to solve the classic N+1 problem when
    // listing employees together with their department in one query.
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department " +
           "WHERE (:keyword IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Employee> search(@Param("keyword") String keyword, Pageable pageable);
}
