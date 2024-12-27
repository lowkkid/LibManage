package com.libmanage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class DepartmentDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("employeeCount")
    private Long employeeCount;

    @JsonProperty("averageSalary")
    private Double averageSalary;

    public DepartmentDto(Integer id, String name, Long employeeCount, Double averageSalary) {
        this.id = id;
        this.name = name;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
    }

    public DepartmentDto() {
    }

    public Double getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(Double averageSalary) {
        this.averageSalary = averageSalary;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }
}
