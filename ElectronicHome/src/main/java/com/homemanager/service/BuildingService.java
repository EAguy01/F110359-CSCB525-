
// BuildingService.java
package com.homemanager.service;

import com.homemanager.dao.BuildingDAO;
import com.homemanager.dao.EmployeeDAO;
import com.homemanager.model.Building;
import com.homemanager.model.Employee;
import com.homemanager.util.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class BuildingService {
    private BuildingDAO buildingDAO;
    private EmployeeDAO employeeDAO;

    public BuildingService() {
        this.buildingDAO = new BuildingDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    public void createBuilding(Building building) throws ValidationException, SQLException {
        validateBuilding(building);
        if (building.getEmployeeId() == null) {

            Employee employee = employeeDAO.findLeastAssignedEmployee(1); // TODO: Pass company ID
            if (employee != null) {
                building.setEmployeeId(employee.getEmployeeId());
            }
        }
        buildingDAO.create(building);
    }

    public Building getBuildingById(Integer id) throws SQLException {
        return buildingDAO.findById(id);
    }
// ne se izpolzva
//    public List<Building> getBuildingsByEmployee(Integer employeeId) throws SQLException {
//        return buildingDAO.findByEmployee(employeeId);
//    }

    public void updateBuilding(Building building) throws ValidationException, SQLException {
        validateBuilding(building);
        buildingDAO.update(building);
    }


    public List<Building> getAllBuildings() throws SQLException {
        return buildingDAO.findAll();
    }

    public void deleteBuilding(Integer id) throws SQLException {
        buildingDAO.delete(id);
    }

    private void validateBuilding(Building building) throws ValidationException {
        if (building.getAddress() == null || building.getAddress().trim().isEmpty()) {
            throw new ValidationException("Building address cannot be empty");
        }
        if (building.getTotalFloors() == null || building.getTotalFloors() <= 0) {
            throw new ValidationException("Invalid number of floors");
        }
        if (building.getTotalApartments() == null || building.getTotalApartments() <= 0) {
            throw new ValidationException("Invalid number of apartments");
        }
        if (building.getTotalArea() == null || building.getTotalArea().signum() <= 0) {
            throw new ValidationException("Invalid total area");
        }
    }
}