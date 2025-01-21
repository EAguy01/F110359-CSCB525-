package com.homemanager.service;

import com.homemanager.dao.ApartmentDAO;
import com.homemanager.dao.BuildingDAO;
import com.homemanager.dao.ResidentDAO;
import com.homemanager.dao.ApartmentOwnershipDAO;
import com.homemanager.model.Apartment;
import com.homemanager.model.Building;
import com.homemanager.util.ValidationException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ApartmentService {
    private ApartmentDAO apartmentDAO;
    private BuildingDAO buildingDAO;
    private ResidentDAO residentDAO;
    private ApartmentOwnershipDAO ownershipDAO;

    public ApartmentService() {
        this.apartmentDAO = new ApartmentDAO();
        this.buildingDAO = new BuildingDAO();
        this.residentDAO = new ResidentDAO();
        this.ownershipDAO = new ApartmentOwnershipDAO();
    }

    public void createApartment(Apartment apartment) throws ValidationException, SQLException {
        validateApartment(apartment);
        validateBuildingCapacity(apartment.getBuildingId());
        validateUniqueApartmentNumber(apartment);
        apartmentDAO.create(apartment);
    }

    public Apartment getApartmentById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Apartment ID cannot be null");
        }
        return apartmentDAO.findById(id);
    }

    public void deleteApartmentOwner(Integer id) throws SQLException, ValidationException {
        if (ownershipDAO.findCurrentOwnership(id) != null) {
            throw new ValidationException("Cannot delete apartment with active ownership.");
        }
        apartmentDAO.delete(id);
    }

    public List<Apartment> getApartmentsByBuilding(Integer buildingId) throws SQLException {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }
        return apartmentDAO.findByBuilding(buildingId);
    }

//    public List<Apartment> getVacantApartments(Integer buildingId) throws SQLException {
//        if (buildingId == null) {
//            throw new IllegalArgumentException("Building ID cannot be null");
//        }
//        return apartmentDAO.findVacantApartments(buildingId);
//    }

    public void updateApartment(Apartment apartment) throws ValidationException, SQLException {
        if (apartment == null || apartment.getApartmentId() == null) {
            throw new IllegalArgumentException("Invalid apartment data");
        }

        Apartment existingApartment = apartmentDAO.findById(apartment.getApartmentId());
        if (existingApartment == null) {
            throw new ValidationException("Apartment does not exist");
        }

        validateApartment(apartment);
        validateUniqueApartmentNumber(apartment);

        // Check if there are any residents before allowing floor change
        if (!existingApartment.getFloorNumber().equals(apartment.getFloorNumber())) {
            int residentCount = residentDAO.getResidentCount(apartment.getApartmentId());
            if (residentCount > 0) {
                throw new ValidationException("Cannot change floor number while apartment has residents");
            }
        }

        apartmentDAO.update(apartment);
    }

    public void deleteApartment(Integer id) throws SQLException, ValidationException {
        if (id == null) {
            throw new IllegalArgumentException("Apartment ID cannot be null");
        }

        // Check if apartment has residents
        int residentCount = residentDAO.getResidentCount(id);
        if (residentCount > 0) {
            throw new ValidationException("Cannot delete apartment with active residents");
        }

        // Check if apartment has current ownership
        if (ownershipDAO.findCurrentOwnership(id) != null) {
            throw new ValidationException("Cannot delete apartment with active ownership");
        }

        apartmentDAO.delete(id);
    }

    public BigDecimal getTotalArea(Integer buildingId) throws SQLException {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }
        return apartmentDAO.calculateTotalArea(buildingId);
    }

    private void validateApartment(Apartment apartment) throws ValidationException, SQLException {
        if (apartment == null) {
            throw new ValidationException("Apartment cannot be null");
        }

        if (apartment.getBuildingId() == null) {
            throw new ValidationException("Building ID cannot be null");
        }

        if (apartment.getApartmentNumber() == null || apartment.getApartmentNumber().trim().isEmpty()) {
            throw new ValidationException("Apartment number cannot be empty");
        }

        if (!apartment.getApartmentNumber().matches("^[A-Za-z0-9-]{1,10}$")) {
            throw new ValidationException("Invalid apartment number format");
        }

        if (apartment.getFloorNumber() == null || apartment.getFloorNumber() <= 0) {
            throw new ValidationException("Invalid floor number");
        }

        if (apartment.getArea() == null || apartment.getArea().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid apartment area");
        }

        // Validate against building constraints
        Building building = buildingDAO.findById(apartment.getBuildingId());
        if (building == null) {
            throw new ValidationException("Invalid building ID");
        }

        if (apartment.getFloorNumber() > building.getTotalFloors()) {
            throw new ValidationException(
                    String.format("Floor number %d exceeds building's total floors (%d)",
                            apartment.getFloorNumber(), building.getTotalFloors())
            );
        }
    }

    private void validateBuildingCapacity(Integer buildingId) throws SQLException, ValidationException {
        Building building = buildingDAO.findById(buildingId);
        int currentApartmentCount = apartmentDAO.getApartmentCount(buildingId);

        if (currentApartmentCount >= building.getTotalApartments()) {
            throw new ValidationException(
                    String.format("Building has reached its maximum capacity of %d apartments",
                            building.getTotalApartments())
            );
        }
    }

    private void validateUniqueApartmentNumber(Apartment apartment) throws SQLException, ValidationException {
        List<Apartment> buildingApartments = apartmentDAO.findByBuilding(apartment.getBuildingId());
        for (Apartment existingApartment : buildingApartments) {
            if (!existingApartment.getApartmentId().equals(apartment.getApartmentId()) &&
                    existingApartment.getApartmentNumber().equalsIgnoreCase(apartment.getApartmentNumber().trim())) {
                throw new ValidationException(
                        String.format("Apartment number %s already exists in this building",
                                apartment.getApartmentNumber())
                );
            }
        }
    }

}