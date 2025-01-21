package com.homemanager.dao;

import com.homemanager.model.Company;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO extends BaseDAO {

    // total revenue za kompaniqta


    public void updateRevenue(Integer companyId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE companies SET total_revenue = total_revenue + ? WHERE company_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, companyId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating company revenue failed, no company found with ID: " + companyId);
            }
        }
    }
    // nova kompaniq v bazata

    public void create(Company company) throws SQLException {
        String sql = "INSERT INTO companies (company_name, address, contact_number, email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, company.getCompanyName());
            stmt.setString(2, company.getAddress());
            stmt.setString(3, company.getContactNumber());
            stmt.setString(4, company.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating company failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    company.setCompanyId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating company failed, no ID obtained.");
                }
            }
        }
    }
    // namirame kopnaiqta po ID

    public Company findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM companies WHERE company_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompany(rs);
                }
            }
        }
        return null;
    }
    // vzimame vsichki kompanie podredeni po ime

    public List<Company> findAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies ORDER BY company_name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }
        }
        return companies;
    }
    // promenqme kompaniqta v bazata

    public void update(Company company) throws SQLException {
        String sql = "UPDATE companies SET company_name = ?, address = ?, " +
                "contact_number = ?, email = ? WHERE company_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, company.getCompanyName());
            stmt.setString(2, company.getAddress());
            stmt.setString(3, company.getContactNumber());
            stmt.setString(4, company.getEmail());
            stmt.setInt(5, company.getCompanyId());

            stmt.executeUpdate();
        }
    }
    // triem kompaniqta ot bazata

    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM companies WHERE company_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    // pravim obekt

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setCompanyId(rs.getInt("company_id"));
        company.setCompanyName(rs.getString("company_name"));
        company.setAddress(rs.getString("address"));
        company.setContactNumber(rs.getString("contact_number"));
        company.setEmail(rs.getString("email"));
        company.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        return company;
    }
}