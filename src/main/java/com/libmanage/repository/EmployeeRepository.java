package com.libmanage.repository;

import com.libmanage.dto.EmployeeDto;
import com.libmanage.model.Employee;
import com.libmanage.model.User;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsById(Integer id);

    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.department.id = :departmentId")
    Optional<Double> findAverageSalaryByDepartmentId(@Param("departmentId") Integer departmentId);

    @Query("""
            SELECT new com.libmanage.dto.EmployeeDto(
                e.id,
                u.username,
                d.name,
                e.salary,
                e.active
            )
            FROM Employee e
            JOIN e.user u
            JOIN e.department d
            WHERE (:departmentId IS NULL OR d.id = :departmentId)
            """)
    List<EmployeeDto> findEmployeesDtosByDepartment(@Param("departmentId") Integer departmentId);


    @Query("""
            SELECT e
            FROM Employee e
            JOIN e.user u
            JOIN e.department d
            WHERE (:departmentId IS NULL OR d.id = :departmentId)
            """)
    List<Employee> findEmployeesByDepartmentId(@Param("departmentId") Integer departmentId);

    @Modifying
    @Query("UPDATE Employee e SET e.salary = e.salary * :adjustmentFactor WHERE e.department.id = :departmentId")
    void adjustSalariesByDepartment(Integer departmentId, Double adjustmentFactor);

    @Query(value = "SELECT id FROM employees WHERE user_id = :userId", nativeQuery = true)
    Integer findEmployeeIdByUser(@Param("userId") Integer userId);
}