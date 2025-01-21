package com.homemanager.dao;

import com.homemanager.model.Payment;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class PaymentDAO extends BaseDAO {
// nov payment v bazata
    public void create(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (apartment_id, amount, payment_date, payment_month, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getApartmentId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            stmt.setDate(4, Date.valueOf(payment.getPaymentMonth()));
            stmt.setString(5, payment.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }
// vzima idto
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setPaymentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
        }
    }
// namira plashtane po id
    public Payment findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }
        return null;
    }
    // namira i vrushta vsichki plashtaniq za specifichen apartament

    public List<Payment> findByApartment(Integer apartmentId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE apartment_id = ? ORDER BY payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }
        return payments;
    }
    // namira i vrushta vsichki plashtaniq za specifichen apartament // spoired datata

    public List<Payment> findByApartmentAndDateRange(Integer apartmentId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE apartment_id = ? AND payment_date BETWEEN ? AND ? " +
                "ORDER BY payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }
        return payments;
    }
    // platejni sprqmo kompaniq spored datata

    public List<Payment> findByCompanyAndDateRange(Integer companyId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "JOIN buildings b ON a.building_id = b.building_id " +
                "JOIN employees e ON b.employee_id = e.employee_id " +
                "WHERE e.company_id = ? AND p.payment_date BETWEEN ? AND ? " +
                "ORDER BY p.payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }
        return payments;
    }
    // namira i vrushta vsichki platejni s "pending" status.

    public List<Payment> findPendingPayments() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE status = 'PENDING'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        }
        return payments;
    }
    // propmenq sushtestvuvasht payment record v bazata

    public void update(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET apartment_id = ?, amount = ?, payment_date = ?, " +
                "payment_month = ?, status = ? WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, payment.getApartmentId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            stmt.setDate(4, Date.valueOf(payment.getPaymentMonth()));
            stmt.setString(5, payment.getStatus());
            stmt.setInt(6, payment.getPaymentId());

            stmt.executeUpdate();
        }
    }
// smqta i vrushta vsiuchki plashtaniq za kompaniq spored datata
    public BigDecimal calculateTotalPaymentsForCompany(Integer companyId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.amount), 0) as total FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "JOIN buildings b ON a.building_id = b.building_id " +
                "JOIN employees e ON b.employee_id = e.employee_id " +
                "WHERE e.company_id = ? AND p.payment_date BETWEEN ? AND ? " +
                "AND p.status = 'PAID'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }
    //smqta i vrushta  za sgrada spored datata

    public BigDecimal calculateTotalPaymentsForBuilding(Integer buildingId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.amount), 0) as total FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "WHERE a.building_id = ? AND p.payment_date BETWEEN ? AND ? " +
                "AND p.status = 'PAID'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }
    //vrushta id na kompaniqta svurzana s platejnoto na apartamenta

    public Integer getCompanyIdForPayment(Integer apartmentId) throws SQLException {
        String sql = "SELECT e.company_id FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "JOIN buildings b ON a.building_id = b.building_id " +
                "JOIN employees e ON b.employee_id = e.employee_id " +
                "WHERE p.apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("company_id");
                }
            }
        }
        return null;
    }
    //vrushta imeto na kompaniqta svcurzana s plashtaneto na apartamenta

    public String getCompanyNameForPayment(Integer apartmentId) throws SQLException {
        String sql = "SELECT c.company_name FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "JOIN buildings b ON a.building_id = b.building_id " +
                "JOIN employees e ON b.employee_id = e.employee_id " +
                "JOIN companies c ON e.company_id = c.company_id " +
                "WHERE p.apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("company_name");
                }
            }
        }
        return null;
    }

    public String getEmployeeNameForPayment(Integer apartmentId) throws SQLException {
        String sql = "SELECT CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                "FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "JOIN buildings b ON a.building_id = b.building_id " +
                "JOIN employees e ON b.employee_id = e.employee_id " +
                "WHERE p.apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("employee_name");
                }
            }
        }
        return null;
    }

    public String getBuildingAddressForPayment(Integer apartmentId) throws SQLException {
        String sql = "SELECT b.address FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "JOIN buildings b ON a.building_id = b.building_id " +
                "WHERE p.apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("address");
                }
            }
        }
        return null;
    }

    public String getApartmentNumberForPayment(Integer apartmentId) throws SQLException {
        String sql = "SELECT apartment_number FROM apartments WHERE apartment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("apartment_number");
                }
            }
        }
        return null;
    }

    // za sega ne se izolzva
    public List<Payment> findByBuildingAndDateRange(Integer buildingId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                "JOIN apartments a ON p.apartment_id = a.apartment_id " +
                "WHERE a.building_id = ? AND p.payment_date BETWEEN ? AND ? " +
                "ORDER BY p.payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buildingId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }
        return payments;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setApartmentId(rs.getInt("apartment_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        payment.setPaymentMonth(rs.getDate("payment_month").toLocalDate());
        payment.setStatus(rs.getString("status"));
        return payment;
    }
}