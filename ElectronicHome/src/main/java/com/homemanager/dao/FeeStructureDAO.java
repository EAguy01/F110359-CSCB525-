package com.homemanager.dao;

import com.homemanager.model.FeeStructure;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FeeStructureDAO extends BaseDAO {
    // pravi novi platejni i prekratqva stari

    public void create(FeeStructure feeStructure) throws SQLException {
        String sql = "INSERT INTO fee_structure (building_id, base_rate_per_sqm, elevator_fee_per_person, " +
                "pet_fee, effective_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, feeStructure.getBuildingId());
            stmt.setBigDecimal(2, feeStructure.getBaseRatePerSqm());
            stmt.setBigDecimal(3, feeStructure.getElevatorFeePerPerson());
            stmt.setBigDecimal(4, feeStructure.getPetFee());
            stmt.setDate(5, Date.valueOf(feeStructure.getEffectiveDate()));
            stmt.setDate(6, feeStructure.getEndDate() != null ?
                    Date.valueOf(feeStructure.getEndDate()) : null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating fee structure failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    feeStructure.setFeeId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating fee structure failed, no ID obtained.");
                }
            }

            // tuk prekratqvame predishna ako e nujno
            if (feeStructure.getEffectiveDate() != null) {
                endPreviousFeeStructure(feeStructure.getBuildingId(),
                        feeStructure.getEffectiveDate().minusDays(1));
            }
        }
    }
    // namira po id platejno

    public FeeStructure findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM fee_structure WHERE fee_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeeStructure(rs);
                }
            }
        }
        return null;
    }
    //namira paltejno za sgradata spored datata

    public FeeStructure findCurrentFee(Integer buildingId) throws SQLException {
        String sql = """
        SELECT * FROM fee_structure
        WHERE building_id = ?
        AND effective_date <= CURRENT_DATE
        AND (end_date IS NULL OR end_date >= CURRENT_DATE)
        ORDER BY effective_date DESC LIMIT 1;
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeeStructure(rs);
                }
            }
        }
        return null;
    }


    /*
    public FeeStructure findCurrentFee(Integer buildingId) throws SQLException {
        String sql = "SELECT * FROM fee_structure " +
                "WHERE building_id = ? " +
                "AND effective_date <= CURRENT_DATE " +
                "AND (end_date IS NULL OR end_date >= CURRENT_DATE) " +
                "ORDER BY effective_date DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeeStructure(rs);
                }
            }
        }
        return null;
    }
*/

    // namira vishcki platejni za sgrada spored datata

    public List<FeeStructure> findByBuilding(Integer buildingId) throws SQLException {
        List<FeeStructure> feeStructures = new ArrayList<>();
        String sql = "SELECT * FROM fee_structure WHERE building_id = ? ORDER BY effective_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feeStructures.add(mapResultSetToFeeStructure(rs));
                }
            }
        }
        return feeStructures;
    }
    // Finds and returns all active fee structures based on the current date.
// namira i vrushta vsichki activni polatejni spored datata
    public List<FeeStructure> findActiveFeeStructures() throws SQLException {
        List<FeeStructure> feeStructures = new ArrayList<>();
        String sql = "SELECT * FROM fee_structure " +
                "WHERE effective_date <= CURRENT_DATE " +
                "AND (end_date IS NULL OR end_date >= CURRENT_DATE)";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                feeStructures.add(mapResultSetToFeeStructure(rs));
            }
        }
        return feeStructures;
    }
    //dava novid etaili na platejnoto

    public void update(FeeStructure feeStructure) throws SQLException {
        String sql = "UPDATE fee_structure SET building_id = ?, base_rate_per_sqm = ?, " +
                "elevator_fee_per_person = ?, pet_fee = ?, effective_date = ?, end_date = ? " +
                "WHERE fee_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, feeStructure.getBuildingId());
            stmt.setBigDecimal(2, feeStructure.getBaseRatePerSqm());
            stmt.setBigDecimal(3, feeStructure.getElevatorFeePerPerson());
            stmt.setBigDecimal(4, feeStructure.getPetFee());
            stmt.setDate(5, Date.valueOf(feeStructure.getEffectiveDate()));
            stmt.setDate(6, feeStructure.getEndDate() != null ?
                    Date.valueOf(feeStructure.getEndDate()) : null);
            stmt.setInt(7, feeStructure.getFeeId());

            stmt.executeUpdate();
        }
    }
    // ttrie po id

    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM fee_structure WHERE fee_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    //proverqva ako ima nqkakvi overlapping platejni za sgrda (ne se izpozlva za sega)

    public boolean hasOverlappingFeeStructure(Integer buildingId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM fee_structure " +
                "WHERE building_id = ? " +
                "AND effective_date <= ? " +
                "AND (end_date IS NULL OR end_date >= ?) " +
                "AND fee_id != ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);
            stmt.setDate(2, Date.valueOf(endDate != null ? endDate : LocalDate.now()));
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setInt(4, 0); // For new fee structures

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private void endPreviousFeeStructure(Integer buildingId, LocalDate endDate) throws SQLException {
        String sql = "UPDATE fee_structure SET end_date = ? " +
                "WHERE building_id = ? AND end_date IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(endDate));
            stmt.setInt(2, buildingId);
            stmt.executeUpdate();
        }
    }

    //ne se izpozlva za sega
    public FeeStructure getFeeStructureForDate(Integer buildingId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM fee_structure " +
                "WHERE building_id = ? " +
                "AND effective_date <= ? " +
                "AND (end_date IS NULL OR end_date >= ?) " +
                "ORDER BY effective_date DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setDate(3, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeeStructure(rs);
                }
            }
        }
        return null;
    }
    // vrushta istoriqta na platejnite za sgrada (ne se ipozlva za sega)

    public List<FeeStructure> getFeeHistory(Integer buildingId) throws SQLException {
        List<FeeStructure> feeHistory = new ArrayList<>();
        String sql = "SELECT * FROM fee_structure " +
                "WHERE building_id = ? " +
                "ORDER BY effective_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feeHistory.add(mapResultSetToFeeStructure(rs));
                }
            }
        }
        return feeHistory;
    }

    private FeeStructure mapResultSetToFeeStructure(ResultSet rs) throws SQLException {
        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setFeeId(rs.getInt("fee_id"));
        feeStructure.setBuildingId(rs.getInt("building_id"));
        feeStructure.setBaseRatePerSqm(rs.getBigDecimal("base_rate_per_sqm"));
        feeStructure.setElevatorFeePerPerson(rs.getBigDecimal("elevator_fee_per_person"));
        feeStructure.setPetFee(rs.getBigDecimal("pet_fee"));
        feeStructure.setEffectiveDate(rs.getDate("effective_date").toLocalDate());

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            feeStructure.setEndDate(endDate.toLocalDate());
        }

        return feeStructure;
    }

}