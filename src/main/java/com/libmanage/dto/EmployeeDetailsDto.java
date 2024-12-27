package com.libmanage.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class EmployeeDetailsDto {

    @JsonProperty("username")
    private String username;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("pastSalaries")
    private List<SalaryDto> pastSalaries;

    @JsonProperty("upcomingSalaries")
    private List<SalaryDto> upcomingSalaries;

    public EmployeeDetailsDto() {
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

    public List<SalaryDto> getPastSalaries() {
        return pastSalaries;
    }

    public void setPastSalaries(List<SalaryDto> pastSalaries) {
        this.pastSalaries = pastSalaries;
    }

    public List<SalaryDto> getUpcomingSalaries() {
        return upcomingSalaries;
    }

    public void setUpcomingSalaries(List<SalaryDto> upcomingSalaries) {
        this.upcomingSalaries = upcomingSalaries;
    }
}
