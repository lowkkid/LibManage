package com.libmanage.repository;

import com.libmanage.model.EmployeeTransferLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeTransferLogRepository extends JpaRepository<EmployeeTransferLog, Integer> {
}
