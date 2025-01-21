package com.homemanager.dao;

import com.homemanager.model.Resident;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResidentDAO extends BaseDAO {

    public void create(Resident resident) throws SQLException {
        String sql = "INSERT INTO residents (apartment_id, first_name, last_name, date_of_birth, has_pet) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, resident.getApartmentId());
            stmt.setString(2, resident.getFirstName());
            stmt.setString(3, resident.getLastName());
            stmt.setDate(4, Date.valueOf(resident.getDateOfBirth()));
            stmt.setBoolean(5, resident.getHasPet());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating resident failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    resident.setResidentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating resident failed, no ID obtained.");
                }
            }
        }
    }

    public Resident findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM residents WHERE resident_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToResident(rs);
                }
            }
        }
        return null;
    }

    public List<Resident> findByApartment(Integer apartmentId) throws SQLException {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    residents.add(mapResultSetToResident(rs));
                }
            }
        }
        return residents;
    }

    public List<Resident> findByBuilding(Integer buildingId) throws SQLException {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT r.* FROM residents r " +
                "JOIN apartments a ON r.apartment_id = a.apartment_id " +
                "WHERE a.building_id = ? " +
                "ORDER BY r.last_name, r.first_name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    residents.add(mapResultSetToResident(rs));
                }
            }
        }
        return residents;
    }
// za sega ne se izpozlva

    public List<Resident> findResidentsWithPets(Integer buildingId) throws SQLException {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT r.* FROM residents r " +
                "JOIN apartments a ON r.apartment_id = a.apartment_id " +
                "WHERE a.building_id = ? AND r.has_pet = true " +
                "ORDER BY a.apartment_number";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    residents.add(mapResultSetToResident(rs));
                }
            }
        }
        return residents;
    }
// za sega ne se izpozlva

    public List<Resident> findResidentsByAgeRange(Integer buildingId, int minAge, int maxAge) throws SQLException {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT r.* FROM residents r " +
                "JOIN apartments a ON r.apartment_id = a.apartment_id " +
                "WHERE a.building_id = ? " +
                "AND TIMESTAMPDIFF(YEAR, r.date_of_birth, CURDATE()) BETWEEN ? AND ? " +
                "ORDER BY r.date_of_birth";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);
            stmt.setInt(2, minAge);
            stmt.setInt(3, maxAge);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    residents.add(mapResultSetToResident(rs));
                }
            }
        }
        return residents;
    }
//// za sega ne se izpozlva
//    public List<Resident> findEligibleForElevatorFee(Integer buildingId) throws SQLException {
//        List<Resident> residents = new ArrayList<>();
//        String sql = "SELECT r.* FROM residents r " +
//                "JOIN apartments a ON r.apartment_id = a.apartment_id " +
//                "JOIN buildings b ON a.building_id = b.building_id " +
//                "WHERE b.building_id = ? " +
//                "AND b.has_elevator = true " +
//                "AND TIMESTAMPDIFF(YEAR, r.date_of_birth, CURDATE()) >= 7 " +
//                "ORDER BY a.apartment_number";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, buildingId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    residents.add(mapResultSetToResident(rs));
//                }
//            }
//        }
//        return residents;
//    }

    public void update(Resident resident) throws SQLException {
        String sql = "UPDATE residents SET apartment_id = ?, first_name = ?, last_name = ?, " +
                "date_of_birth = ?, has_pet = ? WHERE resident_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, resident.getApartmentId());
            stmt.setString(2, resident.getFirstName());
            stmt.setString(3, resident.getLastName());
            stmt.setDate(4, Date.valueOf(resident.getDateOfBirth()));
            stmt.setBoolean(5, resident.getHasPet());
            stmt.setInt(6, resident.getResidentId());

            stmt.executeUpdate();
        }
    }

    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM residents WHERE resident_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public int getResidentCount(Integer buildingId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM residents r " +
                "JOIN apartments a ON r.apartment_id = a.apartment_id " +
                "WHERE a.building_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
//// za sega ne se izpozlva
//    public int getPetCount(Integer buildingId) throws SQLException {
//        String sql = "SELECT COUNT(*) as count FROM residents r " +
//                "JOIN apartments a ON r.apartment_id = a.apartment_id " +
//                "WHERE a.building_id = ? AND r.has_pet = true";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, buildingId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("count");
//                }
//            }
//        }
//        return 0;
//    }

    private Resident mapResultSetToResident(ResultSet rs) throws SQLException {
        Resident resident = new Resident();
        resident.setResidentId(rs.getInt("resident_id"));
        resident.setApartmentId(rs.getInt("apartment_id"));
        resident.setFirstName(rs.getString("first_name"));
        resident.setLastName(rs.getString("last_name"));
        resident.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        resident.setHasPet(rs.getBoolean("has_pet"));
        return resident;
    }



}