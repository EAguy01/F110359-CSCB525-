package com.homemanager.service;

import com.homemanager.dao.EmployeeDAO;
import com.homemanager.dao.BuildingDAO;
import com.homemanager.model.Employee;
import com.homemanager.util.ValidationException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmployeeService {
    private EmployeeDAO employeeDAO;
    private BuildingDAO buildingDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.buildingDAO = new BuildingDAO();
    }

    public void createEmployee(Employee employee) throws ValidationException, SQLException {
        validateEmployee(employee);
        employeeDAO.create(employee);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.findAll();
    }

    public Employee getEmployeeById(Integer id) throws SQLException {
        return employeeDAO.findById(id);
    }
// ne se izpolzva

    public List<Employee> getEmployeesByCompany(Integer companyId) throws SQLException {
        return employeeDAO.findByCompany(companyId);
    }

    public void updateEmployee(Employee employee) throws ValidationException, SQLException {
        validateEmployee(employee);
        employeeDAO.update(employee);
    }

    public void deleteEmployee(Integer employeeId) throws SQLException {
        employeeDAO.delete(employeeId);
    }

    public void terminateEmployee(Integer employeeId) throws SQLException {
        Employee employee = getEmployeeById(employeeId);
        if (employee != null) {
            Employee newEmployee = employeeDAO.findLeastAssignedEmployee(employee.getCompanyId());
            if (newEmployee != null) {
                buildingDAO.reassignBuildings(employeeId, newEmployee.getEmployeeId());
            }
            employee.setStatus("INACTIVE");
            try {
                updateEmployee(employee);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void validateEmployee(Employee employee) throws ValidationException {
        if (employee.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name cannot be empty");
        }
        if (employee.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name cannot be empty");
        }
        if (employee.getEmail() != null && !employee.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
        if (employee.getContactNumber() != null && !employee.getContactNumber().matches("\\d{10}")) {
            throw new ValidationException("Contact number must be 10 digits");
        }
        if (employee.getHireDate() == null || employee.getHireDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Invalid hire date");
        }
    }
}
