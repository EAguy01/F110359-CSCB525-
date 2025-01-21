// Employee.java
package com.homemanager.model;

import java.time.LocalDate;

public class Employee {
    private Integer employeeId;
    private Integer companyId;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String email;
    private LocalDate hireDate;
    private String status; // ACTIVE, INACTIVE

    public Employee() {
        this.status = "ACTIVE";
        this.hireDate = LocalDate.now();
    }

    public Employee(Integer companyId, String firstName, String lastName, String contactNumber, String email) {
        this();
        this.companyId = companyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Getters and Setters
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", companyId=" + companyId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", hireDate=" + hireDate +
                ", status='" + status + '\'' +
                '}';
    }
}