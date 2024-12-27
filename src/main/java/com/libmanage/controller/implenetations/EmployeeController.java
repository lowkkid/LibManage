package com.libmanage.controller.implenetations;

import com.libmanage.dto.EmployeeDetailsDto;
import com.libmanage.dto.EmployeeDto;
import com.libmanage.dto.EmployeeOnboardRequest;
import com.libmanage.dto.EmployeeTransferRequest;
import com.libmanage.dto.SalaryDto;
import com.libmanage.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<Void> onboardEmployee(@RequestBody EmployeeOnboardRequest request) {
        employeeService.onboardEmployee(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{employeeId}/transfer")
    public ResponseEntity<Void> transferEmployee(
            @PathVariable Integer employeeId,
            @RequestBody EmployeeTransferRequest request) {
        employeeService.transferEmployee(employeeId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getEmployees(@RequestParam(required = false) Integer departmentId) {
        List<EmployeeDto> employees = employeeService.getEmployees(departmentId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{employeeId}/details")
    public ResponseEntity<EmployeeDetailsDto> getEmployeeDetails(@PathVariable Integer employeeId) {
        EmployeeDetailsDto employeeDetails = employeeService.getEmployeeDetails(employeeId);
        return ResponseEntity.ok(employeeDetails);
    }

    @PutMapping("/salaries/{salaryId}/update-bonus")
    public ResponseEntity<Void> updateSalaryBonus(
            @PathVariable Integer salaryId,
            @RequestParam double bonus) {
        employeeService.updateSalaryBonus(salaryId, bonus);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/salaries/past")
    public ResponseEntity<List<SalaryDto>> getPastSalaries() {
        List<SalaryDto> pastSalaries = employeeService.getPastSalaries();
        return ResponseEntity.ok(pastSalaries);
    }

    @GetMapping("/salaries/next")
    public ResponseEntity<SalaryDto> getNextSalary() {
        SalaryDto nextSalary = employeeService.getNextSalary();
        return ResponseEntity.ok(nextSalary);
    }

}
