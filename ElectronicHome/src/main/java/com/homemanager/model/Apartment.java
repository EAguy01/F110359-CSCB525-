package com.homemanager.model;

import java.math.BigDecimal;

public class Apartment {
    private Integer apartmentId;  //  id apartamewnt
    private Integer buildingId; // sgrada id
    private String apartmentNumber; // nomer na apartament
    private Integer floorNumber; // etaj
    private BigDecimal area;

    // constructopr za obektite s null
    public Apartment() {
    }

    // Constructor with all fields except ID
    public Apartment(Integer buildingId, String apartmentNumber, Integer floorNumber, BigDecimal area) {
        this.buildingId = buildingId;
        this.apartmentNumber = apartmentNumber;
        this.floorNumber = floorNumber;
        this.area = area;
    }

    // getters and setters
    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }
    // pravim obekta na string kato go overridecame

    @Override
    public String toString() {
        return "Apartment{" +
                "apartmentId=" + apartmentId +
                ", buildingId=" + buildingId +
                ", apartmentNumber='" + apartmentNumber + '\'' +
                ", floorNumber=" + floorNumber +
                ", area=" + area +
                '}';
    }
// sravnqvame dali dva apartemnt obekta sa s ednakvo info
    @Override
    public boolean equals(Object o) {
        //chekva daliu sa endkvi
        if (this == o) return true;
        //ako ne e ot type apartament dava false
        if (!(o instanceof Apartment)) return false;

        Apartment apartment = (Apartment) o;
// proverqva dali id i tn sa ednakvi
        if (apartmentId != null ? !apartmentId.equals(apartment.apartmentId) : apartment.apartmentId != null) return false;
        if (buildingId != null ? !buildingId.equals(apartment.buildingId) : apartment.buildingId != null) return false;
        return apartmentNumber != null ? apartmentNumber.equals(apartment.apartmentNumber) : apartment.apartmentNumber == null;
    }

    @Override
    public int hashCode() {
        int result = apartmentId != null ? apartmentId.hashCode() : 0;
        result = 31 * result + (buildingId != null ? buildingId.hashCode() : 0);
        result = 31 * result + (apartmentNumber != null ? apartmentNumber.hashCode() : 0);
        return result;
    }
}