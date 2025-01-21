package com.homemanager.dao;
//Dao class za spravqne s database svurzani s apartamenti
import com.homemanager.model.Apartment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ApartmentDAO extends BaseDAO {
    // pravi apartament v bazata i slaga id

    public void create(Apartment apartment) throws SQLException {
        String sql = "INSERT INTO apartments (building_id, apartment_number, floor_number, area) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, apartment.getBuildingId());
            stmt.setString(2, apartment.getApartmentNumber());
            stmt.setInt(3, apartment.getFloorNumber());
            stmt.setBigDecimal(4, apartment.getArea());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating apartment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    apartment.setApartmentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating apartment failed, no ID obtained.");
                }
            }
        }
    }
    // vzimame apartament po id

    public Apartment findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM apartments WHERE apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToApartment(rs);
                }
            }
        }
        return null;
    }
    // vzima vsichki partamenti ot specifichna sgrada

    public List<Apartment> findByBuilding(Integer buildingId) throws SQLException {
        List<Apartment> apartments = new ArrayList<>();
        String sql = "SELECT * FROM apartments WHERE building_id = ? ORDER BY apartment_number";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apartments.add(mapResultSetToApartment(rs));
                }
            }
        }
        return apartments;
    }
    //vzima vsichki prazni apartamenti v sgrada

    public List<Apartment> findVacantApartments(Integer buildingId) throws SQLException {
        List<Apartment> apartments = new ArrayList<>();
        String sql = "SELECT a.* FROM apartments a " +
                "LEFT JOIN residents r ON a.apartment_id = r.apartment_id " +
                "WHERE a.building_id = ? AND r.resident_id IS NULL " +
                "ORDER BY a.apartment_number";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apartments.add(mapResultSetToApartment(rs));
                }
            }
        }
        return apartments;
    }

    // Updates sushtestuvasht apartament v bazata

    public void update(Apartment apartment) throws SQLException {
        String sql = "UPDATE apartments SET building_id = ?, apartment_number = ?, " +
                "floor_number = ?, area = ? WHERE apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartment.getBuildingId());
            stmt.setString(2, apartment.getApartmentNumber());
            stmt.setInt(3, apartment.getFloorNumber());
            stmt.setBigDecimal(4, apartment.getArea());
            stmt.setInt(5, apartment.getApartmentId());

            stmt.executeUpdate();
        }
    }
    // Deletes apartament po id
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM apartments WHERE apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    // pokazva totalna broika apartamenti v sgrada

    public int getApartmentCount(Integer buildingId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM apartments WHERE building_id = ?";

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
    // smqta total area na csichkite apartamenti v sgrada

    public BigDecimal calculateTotalArea(Integer buildingId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(area), 0) as total_area FROM apartments WHERE building_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_area");
                }
            }
        }
        return BigDecimal.ZERO;
    }
    // apartment opbvjc


    private Apartment mapResultSetToApartment(ResultSet rs) throws SQLException {
        Apartment apartment = new Apartment();
        apartment.setApartmentId(rs.getInt("apartment_id"));
        apartment.setBuildingId(rs.getInt("building_id"));
        apartment.setApartmentNumber(rs.getString("apartment_number"));
        apartment.setFloorNumber(rs.getInt("floor_number"));
        apartment.setArea(rs.getBigDecimal("area"));
        return apartment;
    }
}