package com.homemanager.dao;

import com.homemanager.model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO extends BaseDAO {
    // suzdavame nov zapis na slujitel v bazata danni

    public void create(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (company_id, first_name, last_name, contact_number, email, hire_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, employee.getCompanyId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getContactNumber());
            stmt.setString(5, employee.getEmail());
            stmt.setDate(6, Date.valueOf(employee.getHireDate()));
            stmt.setString(7, employee.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setEmployeeId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        }
    }
// namira employee s nai malko sgradi assigen za dadena kompaniq

    public Employee findLeastAssignedEmployee(Integer companyId) throws SQLException {
        String sql = """
        SELECT e.*, COUNT(b.building_id) AS building_count
        FROM employees e
        LEFT JOIN buildings b ON e.employee_id = b.employee_id
        WHERE e.company_id = ? AND e.status = 'ACTIVE'
        GROUP BY e.employee_id
        ORDER BY building_count ASC
        LIMIT 1
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        }
        return null;
    }
    // vsicvhki aktivni employyes na kompaniq

    public List<Employee> findByCompany(Integer companyId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE company_id = ? AND status = 'ACTIVE'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        }
        return employees;
    }
    // namira employee po id


    public Employee findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        }
        return null;
    }
    // list na vsichki rabotnici v bazata

    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            return employees;
        }
    }
    // promeni rabotnik

    public void update(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET company_id = ?, first_name = ?, last_name = ?, contact_number = ?, email = ?, hire_date = ?, status = ? WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employee.getCompanyId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getContactNumber());
            stmt.setString(5, employee.getEmail());
            stmt.setDate(6, Date.valueOf(employee.getHireDate()));
            stmt.setString(7, employee.getStatus());
            stmt.setInt(8, employee.getEmployeeId());

            stmt.executeUpdate();
        }
    }
    // mahni rabotnik

    public void delete(Integer employeeId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setCompanyId(rs.getInt("company_id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setContactNumber(rs.getString("contact_number"));
        employee.setEmail(rs.getString("email"));
        employee.setHireDate(rs.getDate("hire_date").toLocalDate());
        employee.setStatus(rs.getString("status"));
        return employee;
    }
}
