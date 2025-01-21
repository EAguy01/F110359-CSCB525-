// Payment.java
package com.homemanager.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    // private promenlivi

    private Integer paymentId;
    private Integer apartmentId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private LocalDate paymentMonth;
    private String status; // PENDING, PAID, OVERDUE
    // Sets the status as "PENDING" and the payment date as the current date

    public Payment() {
        this.status = "PENDING";
        this.paymentDate = LocalDate.now();
    }

    public Payment(Integer apartmentId, BigDecimal amount, LocalDate paymentMonth) {
        this();
        this.apartmentId = apartmentId;
        this.amount = amount;
        this.paymentMonth = paymentMonth;
    }

    // Getters and Setters
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDate getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(LocalDate paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", apartmentId=" + apartmentId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMonth=" + paymentMonth +
                ", status='" + status + '\'' +
                '}';
    }
}