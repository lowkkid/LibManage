package com.libmanage.service;

import com.libmanage.dto.DepartmentDto;
import com.libmanage.dto.SalaryAdjustmentRequest;
import com.libmanage.model.Employee;
import com.libmanage.model.Salary;
import com.libmanage.repository.DepartmentRepository;
import com.libmanage.repository.EmployeeRepository;
import com.libmanage.repository.SalaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, SalaryRepository salaryRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
    }

    public List<DepartmentDto> getAllDepartmentsWithEmployeeCount() {
        return departmentRepository.findAllWithEmployeeCountAndAverageSalary();
    }

    @Transactional
    public void adjustSalaries(Integer departmentId, SalaryAdjustmentRequest request) {
        if (request.getAdjustmentFactor() == null || request.getAdjustmentFactor() <= 0) {
            throw new IllegalArgumentException("Adjustment factor must be greater than 0");
        }
        employeeRepository.adjustSalariesByDepartment(departmentId, request.getAdjustmentFactor());
    }


    @Transactional
    public void assignNextSalaryDate(Integer departmentId, LocalDate paymentDate) {
        List<Employee> employees = employeeRepository.findEmployeesByDepartmentId(departmentId);

        if (employees.isEmpty()) {
            throw new IllegalArgumentException("No employees found in department with ID: " + departmentId);
        }

        employees.forEach(employee -> {
            Salary salary = new Salary();
            salary.setEmployee(employee);
            salary.setPaymentDate(paymentDate);
            salary.setAmount(employee.getSalary());
            salaryRepository.save(salary);
        });
    }
}
