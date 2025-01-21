
// ResidentService.java
package com.homemanager.service;

import com.homemanager.dao.ResidentDAO;
import com.homemanager.model.Resident;
import com.homemanager.util.ValidationException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ResidentService {
    private ResidentDAO residentDAO;

    public ResidentService() {
        this.residentDAO = new ResidentDAO();
    }

    public void createResident(Resident resident) throws ValidationException, SQLException {
        validateResident(resident);
        residentDAO.create(resident);
    }

    public Resident getResidentById(Integer id) throws SQLException {
        return residentDAO.findById(id);
    }

    public List<Resident> getResidentsByApartment(Integer apartmentId) throws SQLException {
        return residentDAO.findByApartment(apartmentId);
    }

    public void updateResident(Resident resident) throws ValidationException, SQLException {
        validateResident(resident);
        residentDAO.update(resident);
    }
    public List<Resident> getResidentsByBuilding(Integer buildingId) throws SQLException {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }
        return residentDAO.findByBuilding(buildingId);
    }

    public void deleteResident(Integer id) throws SQLException {
        residentDAO.delete(id);
    }
    public List<Resident> getResidentsWithPets(Integer buildingId) throws SQLException {
        return residentDAO.findResidentsWithPets(buildingId);
    }
    public List<Resident> getResidentsByAgeRange(Integer buildingId, int minAge, int maxAge) throws SQLException {
        return residentDAO.findResidentsByAgeRange(buildingId, minAge, maxAge);
    }
    public int getResidentCountInBuilding(Integer buildingId) throws SQLException {
        return residentDAO.getResidentCount(buildingId);
    }

    public boolean isEligibleForElevatorFee(Resident resident) {
        return Period.between(resident.getDateOfBirth(), LocalDate.now()).getYears() >= 7;
    }

    private void validateResident(Resident resident) throws ValidationException {
        if (resident.getFirstName() == null || resident.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name cannot be empty");
        }
        if (resident.getLastName() == null || resident.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name cannot be empty");
        }
        if (resident.getDateOfBirth() == null || resident.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new ValidationException("Invalid date of birth");
        }
    }
}
