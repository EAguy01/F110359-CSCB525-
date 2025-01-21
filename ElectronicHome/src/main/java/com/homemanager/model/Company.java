package com.homemanager.model;

import java.math.BigDecimal;

public class Company {
    private Integer companyId;
    private String companyName;
    private String address;
    private String contactNumber;
    private String email;
    private BigDecimal totalRevenue;

    public Company() {
        this.totalRevenue = BigDecimal.ZERO;
    }

    public Company(String companyName, String address, String contactNumber, String email) {
        this();
        this.companyName = companyName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Getters and Setters
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ne se izpozlva

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}
