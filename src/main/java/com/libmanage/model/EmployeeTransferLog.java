package com.libmanage.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "employee_transfers")
public class EmployeeTransferLog extends AbstractBaseEntity {

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "old_department_id")
    private Department oldDepartment;

    @ManyToOne
    @JoinColumn(name = "new_department_id", nullable = false)
    private Department newDepartment;

    @Temporal(TemporalType.DATE)
    @Column(name = "transfer_date", nullable = false)
    private Date transferDate;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Department getOldDepartment() {
        return oldDepartment;
    }

    public void setOldDepartment(Department oldDepartment) {
        this.oldDepartment = oldDepartment;
    }

    public Department getNewDepartment() {
        return newDepartment;
    }

    public void setNewDepartment(Department newDepartment) {
        this.newDepartment = newDepartment;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }
}
