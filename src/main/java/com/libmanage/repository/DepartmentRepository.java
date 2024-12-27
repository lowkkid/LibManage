package com.libmanage.repository;

import com.libmanage.dto.DepartmentDto;
import com.libmanage.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query("""
        SELECT new com.libmanage.dto.DepartmentDto(
            d.id,
            d.name,
            COUNT(e.id),
            AVG(e.salary)
        )
        FROM Department d
        LEFT JOIN Employee e ON e.department.id = d.id
        GROUP BY d.id, d.name
        """)
    List<DepartmentDto> findAllWithEmployeeCountAndAverageSalary();
}

