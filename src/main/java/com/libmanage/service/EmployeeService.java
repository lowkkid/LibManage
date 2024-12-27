package com.libmanage.service;

import com.libmanage.config.CustomAuthenticationToken;
import com.libmanage.dto.EmployeeDetailsDto;
import com.libmanage.dto.EmployeeDto;
import com.libmanage.dto.EmployeeOnboardRequest;
import com.libmanage.dto.EmployeeTransferRequest;
import com.libmanage.dto.SalaryDto;
import com.libmanage.exception.EntityNotFoundException;
import com.libmanage.model.Department;
import com.libmanage.model.Employee;
import com.libmanage.model.EmployeeTransferLog;
import com.libmanage.model.Salary;
import com.libmanage.model.User;
import com.libmanage.repository.DepartmentRepository;
import com.libmanage.repository.EmployeeRepository;
import com.libmanage.repository.EmployeeTransferLogRepository;
import com.libmanage.repository.RoleRepository;
import com.libmanage.repository.SalaryRepository;
import com.libmanage.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeTransferLogRepository transferLogRepository;
    private final RoleRepository roleRepository;
    private final SalaryRepository salaryRepository;

    public EmployeeService(UserRepository userRepository,
                           EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           EmployeeTransferLogRepository transferLogRepository, RoleRepository roleRepository, SalaryRepository salaryRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.transferLogRepository = transferLogRepository;
        this.roleRepository = roleRepository;
        this.salaryRepository = salaryRepository;
    }

    @Transactional
    public void onboardEmployee(EmployeeOnboardRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("User with this username already exists");
        }

        // Create User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(roleRepository.findByRoleName("Сотрудник").orElseThrow(() ->new EntityNotFoundException("No such role"))); // Assuming Role is an Enum
        userRepository.save(user);

        // Determine Base Salary
        Double averageSalary = employeeRepository.findAverageSalaryByDepartmentId(request.getDepartmentId())
                .orElse(50000.0);

        // Create Employee
        Employee employee = new Employee();
        employee.setUser(user);
        employee.setDepartment(departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found")));
        employee.setSalary(BigDecimal.valueOf(averageSalary));
        employee.setActive(true);
        employee.setHireDate(LocalDate.now());
        employeeRepository.save(employee);
    }

    @Transactional
    public void transferEmployee(Integer employeeId, EmployeeTransferRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        Department newDepartment = departmentRepository.findById(request.getNewDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("New department not found"));

        Department oldDepartment = employee.getDepartment();
        employee.setDepartment(newDepartment);

        Double averageSalary = employeeRepository.findAverageSalaryByDepartmentId(request.getNewDepartmentId())
                .orElse(50000.0);
        employee.setSalary(BigDecimal.valueOf(averageSalary));
        employeeRepository.save(employee);

        // Log Transfer
        EmployeeTransferLog log = new EmployeeTransferLog();
        log.setEmployee(employee);
        log.setOldDepartment(oldDepartment);
        log.setNewDepartment(newDepartment);
        log.setTransferDate(new java.util.Date());
        transferLogRepository.save(log);
    }

    public List<EmployeeDto> getEmployees(Integer departmentId) {
        return employeeRepository.findEmployeesDtosByDepartment(departmentId);
    }

    Logger logger = Logger.getLogger(this.getClass().getName());

    public EmployeeDetailsDto getEmployeeDetails(Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        List<Salary> pastSalaries = salaryRepository.findPastSalariesByEmployeeId(employeeId, LocalDate.now());
        List<Salary> upcomingSalaries = salaryRepository.findUpcomingSalariesByEmployeeId(employeeId, LocalDate.now());

        return mapToEmployeeDetailsDto(employee, pastSalaries, upcomingSalaries);
    }

    @Transactional
    public void updateSalaryBonus(Integer salaryId, double bonus) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("Salary not found with ID: " + salaryId));
        salaryRepository.updateSalaryBonus(salaryId, bonus);
    }


    private EmployeeDetailsDto mapToEmployeeDetailsDto(Employee employee, List<Salary> pastSalaries, List<Salary> upcomingSalaries) {
        EmployeeDetailsDto detailsDto = new EmployeeDetailsDto();
        detailsDto.setUsername(employee.getUser().getUsername());
        detailsDto.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : "No Department");

        detailsDto.setPastSalaries(
                pastSalaries.stream()
                        .map(s -> new SalaryDto(s.id(), s.getPaymentDate(), s.getAmount().doubleValue(), s.getBonus().doubleValue()))
                        .collect(Collectors.toList())
        );

        upcomingSalaries.forEach(s -> logger.info(s.toString()));
        detailsDto.setUpcomingSalaries(
                upcomingSalaries.stream()
                        .map(s -> new SalaryDto(s.id(), s.getPaymentDate(), s.getAmount().doubleValue(), s.getBonus().doubleValue()))
                        .collect(Collectors.toList())
        );

        for (var x : detailsDto.getUpcomingSalaries()) {
            logger.info(x.toString());
        }

        return detailsDto;
    }

    public List<SalaryDto> getPastSalaries() {
        Integer employeeId = getCurrentEmployeeId();
        List<Salary> salaries = salaryRepository.findPastSalariesByEmployeeId(employeeId);
        return salaries.stream().map(this::mapToDto).toList();
    }

    public SalaryDto getNextSalary() {
        Integer employeeId = getCurrentEmployeeId();
        Optional<Salary> salary = salaryRepository.findNextSalaryByEmployeeId(employeeId);
        return salary.map(this::mapToDto).orElse(null);
    }

    private SalaryDto mapToDto(Salary salary) {
        return new SalaryDto(
                salary.id(),
                salary.getPaymentDate(),
                salary.getAmount().doubleValue(),
                salary.getBonus().doubleValue()
        );
    }

    private Integer getCurrentEmployeeId() {
        Integer currentUserId = getCurrentUserId();
        Integer employeeId = employeeRepository.findEmployeeByUser(
                userRepository.findById(currentUserId).orElseThrow(() -> new EntityNotFoundException("User not found"))
        ).id();
        return employeeId;
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        if (authentication instanceof CustomAuthenticationToken) {
            CustomAuthenticationToken customAuth = (CustomAuthenticationToken) authentication;
            return customAuth.getUserId();
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
            Object principal = auth.getPrincipal();

            UserDetails userDetails = (UserDetails) principal;
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User not found"));
            return user.id();
        }

        throw new IllegalStateException("Unsupported authentication token type");
    }
}
