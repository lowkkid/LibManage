package com.libmanage.repository;

import com.libmanage.model.Salary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Integer> {

    @Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId AND s.paymentDate < :currentDate")
    List<Salary> findPastSalariesByEmployeeId(Integer employeeId, LocalDate currentDate);

    @Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId AND s.paymentDate >= :currentDate")
    List<Salary> findUpcomingSalariesByEmployeeId(Integer employeeId, LocalDate currentDate);

    @Modifying
    @Query("UPDATE Salary s SET s.bonus = :bonus WHERE s.id = :salaryId")
    void updateSalaryBonus(@Param("salaryId") Integer salaryId, @Param("bonus") double bonus);

    @Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId AND s.paymentDate < CURRENT_DATE ORDER BY s.paymentDate DESC")
    List<Salary> findPastSalariesByEmployeeId(@Param("employeeId") Integer employeeId);

    @Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId AND s.paymentDate >= CURRENT_DATE ORDER BY s.paymentDate ASC LIMIT 1")
    Optional<Salary> findNextSalaryByEmployeeId(@Param("employeeId") Integer employeeId);

}
