package com.libmanage.controller.implenetations;

import com.libmanage.dto.DepartmentDto;
import com.libmanage.dto.SalaryAdjustmentRequest;
import com.libmanage.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartmentsWithEmployeeCount();
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/{departmentId}/adjust-salaries")
    public ResponseEntity<Void> adjustSalaries(
            @PathVariable Integer departmentId,
            @RequestBody SalaryAdjustmentRequest request) {
        departmentService.adjustSalaries(departmentId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{departmentId}/assign-salary-date")
    public ResponseEntity<Void> assignSalaryDate(
            @PathVariable Integer departmentId,
            @RequestParam String paymentDate) {
        departmentService.assignNextSalaryDate(departmentId, LocalDate.parse(paymentDate));
        return ResponseEntity.ok().build();
    }
}
