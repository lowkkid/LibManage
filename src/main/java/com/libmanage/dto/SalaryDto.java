package com.libmanage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class SalaryDto {

    @JsonProperty("id")
    private int id;

    @JsonProperty("paymentDate")
    private LocalDate paymentDate;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("bonus")
    private double bonus;

    public SalaryDto(int id, LocalDate paymentDate, double amount, double bonus) {
        this.id = id;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.bonus = bonus;
    }

    public SalaryDto() {
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SalaryDto{" +
                "id=" + id +
                ", paymentDate=" + paymentDate +
                ", amount=" + amount +
                ", bonus=" + bonus +
                '}';
    }
}
