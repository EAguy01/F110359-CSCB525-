package com.homemanager.service;
import com.homemanager.dao.ApartmentOwnershipDAO;
import com.homemanager.model.ApartmentOwnershipManager;
import com.homemanager.util.ValidationException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class ApartmentOwnershipService {
    private ApartmentOwnershipDAO ownershipDAO;

    public ApartmentOwnershipService() {
        this.ownershipDAO = new ApartmentOwnershipDAO();
    }

    public void createOwnership(ApartmentOwnershipManager ownership) throws SQLException, ValidationException {
        if (ownershipDAO.hasOverlappingOwnership(ownership.getApartmentId(), ownership.getStartDate(), null)) {
            throw new ValidationException("Ownership overlap detected.");
        }
        ownershipDAO.create(ownership);
    }

    public List<ApartmentOwnershipManager> getOwnershipHistory(int apartmentId) throws SQLException {
        return ownershipDAO.findByApartment(apartmentId);
    }

    public ApartmentOwnershipManager getCurrentOwnership(int apartmentId) throws SQLException {
        return ownershipDAO.findCurrentOwnership(apartmentId);
    }

    public void endOwnership(int apartmentId, int ownerId, LocalDate endDate) throws SQLException {
        ownershipDAO.endOwnership(apartmentId, ownerId, endDate);
    }
}

