package com.homemanager.dao;

import com.homemanager.model.ApartmentOwnershipManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApartmentOwnershipDAO extends BaseDAO {
    // suzdavane na nov record za pritejanie

    public void create(ApartmentOwnershipManager ownership) throws SQLException {
        String sql = "INSERT INTO apartment_ownership (apartment_id, owner_id, start_date, end_date) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ownership.getApartmentId());
            stmt.setInt(2, ownership.getOwnerId());
            stmt.setDate(3, Date.valueOf(ownership.getStartDate()));
            stmt.setDate(4, ownership.getEndDate() != null ?
                    Date.valueOf(ownership.getEndDate()) : null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating apartment ownership failed, no rows affected.");
            }

            // End previous ownership
            endPreviousOwnership(ownership.getApartmentId(), ownership.getStartDate());
        }
    }
    // pravim novo ownership vurhu startoto

    private void endPreviousOwnership(Integer apartmentId, LocalDate newStartDate) throws SQLException {
        String sql = "UPDATE apartment_ownership SET end_date = ? " +
                "WHERE apartment_id = ? AND end_date IS NULL AND start_date < ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(newStartDate.minusDays(1)));
            stmt.setInt(2, apartmentId);
            stmt.setDate(3, Date.valueOf(newStartDate));
            stmt.executeUpdate();
        }
    }
    // vrushta ownership na specifichen apartament

    public ApartmentOwnershipManager findCurrentOwnership(Integer apartmentId) throws SQLException {
        String sql = "SELECT * FROM apartment_ownership " +
                "WHERE apartment_id = ? AND end_date IS NULL " +
                "ORDER BY start_date DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOwnership(rs);
                }
            }
        }
        return null;
    }
    // vrushta istoriqta na sobstvenost na daden apartament

    public List<ApartmentOwnershipManager> findByApartment(Integer apartmentId) throws SQLException {
        List<ApartmentOwnershipManager> ownerships = new ArrayList<>();
        String sql = "SELECT * FROM apartment_ownership " +
                "WHERE apartment_id = ? ORDER BY start_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ownerships.add(mapResultSetToOwnership(rs));
                }
            }
        }
        return ownerships;
    }
    // vrushta ownership sus speficichen owner

    public List<ApartmentOwnershipManager> findByOwner(Integer ownerId) throws SQLException {
        List<ApartmentOwnershipManager> ownerships = new ArrayList<>();
        String sql = "SELECT * FROM apartment_ownership " +
                "WHERE owner_id = ? ORDER BY start_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ownerships.add(mapResultSetToOwnership(rs));
                }
            }
        }
        return ownerships;
    }
// poakzva owenrships kum chovek
    public List<ApartmentOwnershipManager> findCurrentOwnershipsByOwner(Integer ownerId) throws SQLException {
        List<ApartmentOwnershipManager> ownerships = new ArrayList<>();
        String sql = "SELECT * FROM apartment_ownership " +
                "WHERE owner_id = ? AND end_date IS NULL " +
                "ORDER BY start_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ownerships.add(mapResultSetToOwnership(rs));
                }
            }
        }
        return ownerships;
    }

    // Updates an apartment ownership record

    public void update(ApartmentOwnershipManager ownership) throws SQLException {
        String sql = "UPDATE apartment_ownership SET end_date = ? " +
                "WHERE apartment_id = ? AND owner_id = ? AND start_date = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, ownership.getEndDate() != null ?
                    Date.valueOf(ownership.getEndDate()) : null);
            stmt.setInt(2, ownership.getApartmentId());
            stmt.setInt(3, ownership.getOwnerId());
            stmt.setDate(4, Date.valueOf(ownership.getStartDate()));

            stmt.executeUpdate();
        }
    }
    // prekratqva specifichen ownership

    public void endOwnership(Integer apartmentId, Integer ownerId, LocalDate endDate) throws SQLException {
        String sql = "UPDATE apartment_ownership SET end_date = ? " +
                "WHERE apartment_id = ? AND owner_id = ? AND end_date IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(endDate));
            stmt.setInt(2, apartmentId);
            stmt.setInt(3, ownerId);

            stmt.executeUpdate();
        }
    }
    // proverqva za ownerhsip overlapping v range na datata
    public boolean hasOverlappingOwnership(Integer apartmentId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM apartment_ownership " +
                "WHERE apartment_id = ? AND " +
                "((? BETWEEN start_date AND COALESCE(end_date, CURRENT_DATE)) OR " +
                "(? BETWEEN start_date AND COALESCE(end_date, CURRENT_DATE)) OR " +
                "(start_date BETWEEN ? AND ?))";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, endDate != null ? Date.valueOf(endDate) : Date.valueOf(LocalDate.now()));
            stmt.setDate(4, Date.valueOf(startDate));
            stmt.setDate(5, endDate != null ? Date.valueOf(endDate) : Date.valueOf(LocalDate.now()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    // vryshta info za owenrship za specifichna data
    public ApartmentOwnershipManager findOwnershipAtDate(Integer apartmentId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM apartment_ownership " +
                "WHERE apartment_id = ? " +
                "AND start_date <= ? " +
                "AND (end_date IS NULL OR end_date >= ?) " +
                "ORDER BY start_date DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setDate(3, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOwnership(rs);
                }
            }
        }
        return null;
    }

    //poklazva istoriqta na pritejanie (oshte ne se izpozlva)
    public List<ApartmentOwnershipManager> getOwnershipHistory(Integer apartmentId) throws SQLException {
        List<ApartmentOwnershipManager> history = new ArrayList<>();
        String sql = "SELECT ao.*, o.first_name, o.last_name " +
                "FROM apartment_ownership ao " +
                "JOIN owners o ON ao.owner_id = o.owner_id " +
                "WHERE ao.apartment_id = ? " +
                "ORDER BY ao.start_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapResultSetToOwnership(rs));
                }
            }
        }
        return history;
    }
// pokazva broika pritejavani apartamenti
    public int getOwnedApartmentCount(Integer ownerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM apartment_ownership " +
                "WHERE owner_id = ? AND end_date IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    // maps a result red za object

    private ApartmentOwnershipManager mapResultSetToOwnership(ResultSet rs) throws SQLException {
        ApartmentOwnershipManager ownership = new ApartmentOwnershipManager();
        ownership.setApartmentId(rs.getInt("apartment_id"));
        ownership.setOwnerId(rs.getInt("owner_id"));
        ownership.setStartDate(rs.getDate("start_date").toLocalDate());

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            ownership.setEndDate(endDate.toLocalDate());
        }

        return ownership;
    }
}