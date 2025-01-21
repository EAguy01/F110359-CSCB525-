// Building.java
package com.homemanager.model;

import java.math.BigDecimal;

public class Building {
    private Integer buildingId;
    private Integer employeeId;
    private String address;
    private Integer totalFloors;
    private Integer totalApartments;
    private BigDecimal totalArea;
    private Boolean hasElevator;
    private String commonAreaDescription;

    public Building() {
//        this.hasElevator = false;
    }

    public Building(Integer employeeId, String address, Integer totalFloors, Integer totalApartments,
                    BigDecimal totalArea, Boolean hasElevator, String commonAreaDescription) {
//        this();
        this.employeeId = employeeId;
        this.address = address;
        this.totalFloors = totalFloors;
        this.totalApartments = totalApartments;
        this.totalArea = totalArea;
        this.hasElevator = hasElevator;
        this.commonAreaDescription = commonAreaDescription;
    }


    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(Integer totalFloors) {
        this.totalFloors = totalFloors;
    }

    public Integer getTotalApartments() {
        return totalApartments;
    }

    public void setTotalApartments(Integer totalApartments) {
        this.totalApartments = totalApartments;
    }

    public BigDecimal getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(BigDecimal totalArea) {
        this.totalArea = totalArea;
    }

    public Boolean getHasElevator() {
        return hasElevator;
    }

    public void setHasElevator(Boolean hasElevator) {
        this.hasElevator = hasElevator;
    }

    public String getCommonAreaDescription() {
        return commonAreaDescription;
    }

    public void setCommonAreaDescription(String commonAreaDescription) {
        this.commonAreaDescription = commonAreaDescription;
    }

    @Override
    public String toString() {
        return "Building{" +
                "buildingId=" + buildingId +
                ", employeeId=" + employeeId +
                ", address='" + address + '\'' +
                ", totalFloors=" + totalFloors +
                ", totalApartments=" + totalApartments +
                ", totalArea=" + totalArea +
                ", hasElevator=" + hasElevator +
                ", commonAreaDescription='" + commonAreaDescription + '\'' +
                '}';
    }
}
