
// FeeService.java
package com.homemanager.service;

import com.homemanager.dao.FeeStructureDAO;
import com.homemanager.dao.ResidentDAO;
import com.homemanager.dao.ApartmentDAO;
import com.homemanager.model.FeeStructure;
import com.homemanager.model.Resident;
import com.homemanager.model.Apartment;
import com.homemanager.util.ValidationException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FeeService {
    private FeeStructureDAO feeStructureDAO;
    private ResidentDAO residentDAO;
    private ApartmentDAO apartmentDAO;
    private ResidentService residentService;

    public FeeService() {
        this.feeStructureDAO = new FeeStructureDAO();
        this.residentDAO = new ResidentDAO();
        this.apartmentDAO = new ApartmentDAO();
        this.residentService = new ResidentService();
    }

    public void createFeeStructure(FeeStructure feeStructure) throws ValidationException, SQLException {
        validateFeeStructure(feeStructure);
        feeStructureDAO.create(feeStructure);
    }
    //findById, getBuildingId, getArea()
    public BigDecimal calculateMonthlyFee(Integer apartmentId) throws SQLException {
        Apartment apartment = apartmentDAO.findById(apartmentId);
        if (apartment == null) {
            throw new SQLException("Apartment not found");
        }

        FeeStructure currentFee = feeStructureDAO.findCurrentFee(apartment.getBuildingId());
        if (currentFee == null) {
            throw new SQLException("No active fee structure found");
        }

        // Base fee calculation
        BigDecimal totalFee = apartment.getArea().multiply(currentFee.getBaseRatePerSqm());

        // Add elevator fee for eligible residents
        List<Resident> residents = residentDAO.findByApartment(apartmentId);
        for (Resident resident : residents) {
            if (residentService.isEligibleForElevatorFee(resident)) {
                totalFee = totalFee.add(currentFee.getElevatorFeePerPerson());
            }
            if (resident.getHasPet()) {
                totalFee = totalFee.add(currentFee.getPetFee());
            }
        }

        return totalFee;
    }

    private void validateFeeStructure(FeeStructure feeStructure) throws ValidationException {
        if (feeStructure.getBaseRatePerSqm() == null || feeStructure.getBaseRatePerSqm().signum() <= 0) {
            throw new ValidationException("Invalid base rate");
        }
        if (feeStructure.getElevatorFeePerPerson() == null || feeStructure.getElevatorFeePerPerson().signum() < 0) {
            throw new ValidationException("Invalid elevator fee");
        }
        if (feeStructure.getPetFee() == null || feeStructure.getPetFee().signum() < 0) {
            throw new ValidationException("Invalid pet fee");
        }
        // Allow effective date to be today or future
        if (feeStructure.getEffectiveDate() == null || feeStructure.getEffectiveDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Effective date must be today or in the future");
        }
        if (feeStructure.getEndDate() != null && feeStructure.getEndDate().isBefore(feeStructure.getEffectiveDate())) {
            throw new ValidationException("End date cannot be before effective date");
        }
    }

    public FeeStructure findCurrentFee(Integer buildingId) throws SQLException {
        return feeStructureDAO.findCurrentFee(buildingId);
    }
    public void delete(Integer feeId) throws SQLException {
        feeStructureDAO.delete(feeId);
    }
    public List<FeeStructure> findByBuilding(Integer buildingId) throws SQLException {
        return feeStructureDAO.findByBuilding(buildingId);
    }
    public FeeStructure findById(Integer feeId) throws SQLException {
        return feeStructureDAO.findById(feeId);
    }
    public void updateFeeStructure(FeeStructure feeStructure) throws ValidationException, SQLException {
        validateFeeStructure(feeStructure); // Make sure all data is correct before updating
        feeStructureDAO.update(feeStructure);
    }

}