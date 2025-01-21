
        package com.homemanager.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeeStructure {
    private Integer feeId;
    private Integer buildingId;
    private BigDecimal baseRatePerSqm;
    private BigDecimal elevatorFeePerPerson;
    private BigDecimal petFee;
    private LocalDate effectiveDate;
    private LocalDate endDate;

    public FeeStructure() {
        this.baseRatePerSqm = BigDecimal.ZERO;
        this.elevatorFeePerPerson = BigDecimal.ZERO;
        this.petFee = BigDecimal.ZERO;
        this.effectiveDate = LocalDate.now();
    }

    public FeeStructure(Integer buildingId, BigDecimal baseRatePerSqm,
                        BigDecimal elevatorFeePerPerson, BigDecimal petFee,
                        LocalDate effectiveDate, LocalDate endDate) {
        this.buildingId = buildingId;
        this.baseRatePerSqm = baseRatePerSqm;
        this.elevatorFeePerPerson = elevatorFeePerPerson;
        this.petFee = petFee;
        this.effectiveDate = effectiveDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Integer getFeeId() {
        return feeId;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public BigDecimal getBaseRatePerSqm() {
        return baseRatePerSqm;
    }

    public void setBaseRatePerSqm(BigDecimal baseRatePerSqm) {
        this.baseRatePerSqm = baseRatePerSqm;
    }

    public BigDecimal getElevatorFeePerPerson() {
        return elevatorFeePerPerson;
    }

    public void setElevatorFeePerPerson(BigDecimal elevatorFeePerPerson) {
        this.elevatorFeePerPerson = elevatorFeePerPerson;
    }

    public BigDecimal getPetFee() {
        return petFee;
    }

    public void setPetFee(BigDecimal petFee) {
        this.petFee = petFee;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "FeeStructure{" +
                "feeId=" + feeId +
                ", buildingId=" + buildingId +
                ", baseRatePerSqm=" + baseRatePerSqm +
                ", elevatorFeePerPerson=" + elevatorFeePerPerson +
                ", petFee=" + petFee +
                ", effectiveDate=" + effectiveDate +
                ", endDate=" + endDate +
                '}';
    }
}
