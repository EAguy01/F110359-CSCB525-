
// BuildingDAO.java
package com.homemanager.dao;

import com.homemanager.model.Building;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


//class za interakciite mejdu databaza i modela za sgradi
public class BuildingDAO extends BaseDAO {
    // suzdavame nov record v bazata

    public void create(Building building) throws SQLException {
        String sql = "INSERT INTO buildings (employee_id, address, total_floors, total_apartments, total_area, has_elevator, common_area_description) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, building.getEmployeeId());
            stmt.setString(2, building.getAddress());
            stmt.setInt(3, building.getTotalFloors());
            stmt.setInt(4, building.getTotalApartments());
            stmt.setBigDecimal(5, building.getTotalArea());
            stmt.setBoolean(6, building.getHasElevator());
            stmt.setString(7, building.getCommonAreaDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating building failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    building.setBuildingId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating building failed, no ID obtained.");
                }
            }
        }
    }
    // vzima vsichki sgradi svurzani s daden employee

//    public List<Building> findByEmployee(Integer employeeId) throws SQLException {
//        List<Building> buildings = new ArrayList<>();
//        String sql = "SELECT * FROM buildings WHERE employee_id = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, employeeId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    buildings.add(mapResultSetToBuilding(rs));
//                }
//            }
//        }
//        return buildings;
//    }
    // dava sgrada ot edin rabotnik na drug

    public void reassignBuildings(Integer oldEmployeeId, Integer newEmployeeId) throws SQLException {
        String sql = "UPDATE buildings SET employee_id = ? WHERE employee_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newEmployeeId);
            stmt.setInt(2, oldEmployeeId);
            stmt.executeUpdate();
        }
    }
    // namira sgradata po id

    public Building findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM buildings WHERE building_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBuilding(rs);
                }
            }
        }
        return null;
    }
    // vzima vs sgradi v bazata

    public List<Building> findAll() throws SQLException {
        List<Building> buildings = new ArrayList<>();
        String sql = "SELECT * FROM buildings";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                buildings.add(mapResultSetToBuilding(rs));
            }
        }
        return buildings;
    }
    // promenq sgrada v bazata

    public void update(Building building) throws SQLException {
        String sql = "UPDATE buildings SET employee_id = ?, address = ?, total_floors = ?, " +
                "total_apartments = ?, total_area = ?, has_elevator = ?, " +
                "common_area_description = ? WHERE building_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, building.getEmployeeId());
            stmt.setString(2, building.getAddress());
            stmt.setInt(3, building.getTotalFloors());
            stmt.setInt(4, building.getTotalApartments());
            stmt.setBigDecimal(5, building.getTotalArea());
            stmt.setBoolean(6, building.getHasElevator());
            stmt.setString(7, building.getCommonAreaDescription());
            stmt.setInt(8, building.getBuildingId());

            stmt.executeUpdate();
        }
    }
    // trie sgrada v bazta po id

    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM buildings WHERE building_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    // smqta kolko sgradi sa dadeni na empolyee ( za sega ne se izpolzva)

//    public int getBuildingCountForEmployee(Integer employeeId) throws SQLException {
//        String sql = "SELECT COUNT(*) FROM buildings WHERE employee_id = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, employeeId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//            }
//        }
//        return 0;
//    }
    // zapisva infoto i suzdava obeklt

    private Building mapResultSetToBuilding(ResultSet rs) throws SQLException {
        Building building = new Building();
        building.setBuildingId(rs.getInt("building_id"));
        building.setEmployeeId(rs.getInt("employee_id"));
        building.setAddress(rs.getString("address"));
        building.setTotalFloors(rs.getInt("total_floors"));
        building.setTotalApartments(rs.getInt("total_apartments"));
        building.setTotalArea(rs.getBigDecimal("total_area"));
        building.setHasElevator(rs.getBoolean("has_elevator"));
        building.setCommonAreaDescription(rs.getString("common_area_description"));
        return building;
    }
}
