package com.libmanage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class EmployeeDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("departmentname")
    private String departmentName;

    @JsonProperty("salary")
    private Double salary;

    @JsonProperty("active")
    private Boolean active;

    public EmployeeDto(Integer id, String username, String departmentName, BigDecimal salary, Boolean active) {
        this.id = id;
        this.username = username;
        this.departmentName = departmentName;
        this.salary = salary.doubleValue();
        this.active = active;
    }

    public EmployeeDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
