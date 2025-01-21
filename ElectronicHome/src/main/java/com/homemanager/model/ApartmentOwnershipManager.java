package com.homemanager.model;

import java.time.LocalDate;

public class ApartmentOwnershipManager {
    private Integer apartmentId;
    private Integer ownerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String ownerFirstName;
    private String ownerLastName;
    // Default constructor
    public ApartmentOwnershipManager () {
    }

    // Constructor with all required fields
    public ApartmentOwnershipManager(Integer apartmentId, Integer ownerId, LocalDate startDate) {
        this.apartmentId = apartmentId;
        this.ownerId = ownerId;
        this.startDate = startDate;
    }


    // Getters and Setters
    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    // ne se izpozlva

    public boolean isCurrentlyActive() {
        return endDate == null || endDate.isAfter(LocalDate.now());
    }
    // ne se izpozlva

    public boolean isActive(LocalDate date) {
        return startDate.compareTo(date) <= 0 &&
                (endDate == null || endDate.compareTo(date) >= 0);
    }

    public boolean overlaps(LocalDate start, LocalDate end) {
        return (end == null || startDate.compareTo(end) <= 0) &&
                (endDate == null || end == null || endDate.compareTo(start) >= 0);
    }
    // ne se izpozlva

    public boolean overlaps(ApartmentOwnershipManager other) {
        return overlaps(other.getStartDate(), other.getEndDate());
    }

    public String getOwnerFullName() {
        if (ownerFirstName == null || ownerLastName == null) {
            return null;
        }
        return ownerFirstName + " " + ownerLastName;
    }
    // ne se izpozlva

    public long getDurationInDays() {
        if (endDate == null) {
            return startDate.until(LocalDate.now()).getDays();
        }
        return startDate.until(endDate).getDays();
    }


    @Override
    public String toString() {
        return "ApartmentOwnership{" +
                "apartmentId=" + apartmentId +
                ", ownerId=" + ownerId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", ownerName='" + getOwnerFullName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApartmentOwnershipManager)) return false;

        ApartmentOwnershipManager that = (ApartmentOwnershipManager) o;

        if (!apartmentId.equals(that.apartmentId)) return false;
        if (!ownerId.equals(that.ownerId)) return false;
        return startDate.equals(that.startDate);
    }

    @Override
    public int hashCode() {
        int result = apartmentId.hashCode();
        result = 31 * result + ownerId.hashCode();
        result = 31 * result + startDate.hashCode();
        return result;
    }

    public static class Builder {
        private final ApartmentOwnershipManager ownership;
        // ne se izpozlva

        public Builder(Integer apartmentId, Integer ownerId, LocalDate startDate) {
            ownership = new ApartmentOwnershipManager(apartmentId, ownerId, startDate);
        }    // ne se izpozlva


        public Builder endDate(LocalDate endDate) {
            ownership.setEndDate(endDate);
            return this;
        }
        // ne se izpozlva

        public Builder ownerFirstName(String ownerFirstName) {
            ownership.setOwnerFirstName(ownerFirstName);
            return this;
        }
        // ne se izpozlva

//        public Builder ownerLastName(String ownerLastName) {
//            ownership.setOwnerLastName(ownerLastName);
//            return this;
//        }
        // ne se izpozlva

//        public ApartmentOwnershipManager build() {
//            return ownership;
//        }
    }
}