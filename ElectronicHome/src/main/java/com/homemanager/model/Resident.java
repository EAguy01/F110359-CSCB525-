// Resident.java
package com.homemanager.model;

import java.time.LocalDate;

public class Resident {
    private Integer residentId;
    private Integer apartmentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Boolean hasPet;

    public Resident() {
        this.hasPet = false;
    }

    public Resident(Integer apartmentId, String firstName, String lastName, LocalDate dateOfBirth, Boolean hasPet) {
        this();
        this.apartmentId = apartmentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.hasPet = hasPet;
    }

    // Getters and Setters
    public Integer getResidentId() {
        return residentId;
    }

    public void setResidentId(Integer residentId) {
        this.residentId = residentId;
    }

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getHasPet() {
        return hasPet;
    }

    public void setHasPet(Boolean hasPet) {
        this.hasPet = hasPet;
    }

    // Continuing Resident.java
    @Override
    public String toString() {
        return "Resident{" +
                "residentId=" + residentId +
                ", apartmentId=" + apartmentId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", hasPet=" + hasPet +
                '}';
    }
}