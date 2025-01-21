// CompanyService.java
package com.homemanager.service;

import com.homemanager.dao.CompanyDAO;
import com.homemanager.model.Company;
import com.homemanager.util.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class CompanyService {
    private CompanyDAO companyDAO;

    public CompanyService() {
        this.companyDAO = new CompanyDAO();
    }

    public void createCompany(Company company) throws ValidationException, SQLException {
        validateCompany(company);
        companyDAO.create(company);
    }

    public Company getCompanyById(Integer id) throws SQLException {
        return companyDAO.findById(id);
    }

    public List<Company> getAllCompanies() throws SQLException {
        return companyDAO.findAll();
    }

    public void updateCompany(Company company) throws ValidationException, SQLException {
        validateCompany(company);
        companyDAO.update(company);
    }

    public void deleteCompany(Integer id) throws SQLException {
        companyDAO.delete(id);
    }

    private void validateCompany(Company company) throws ValidationException {
        if (company.getCompanyName() == null || company.getCompanyName().trim().isEmpty()) {
            throw new ValidationException("Company name cannot be empty");
        }
        if (company.getEmail() != null && !company.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
        if (company.getContactNumber() != null && !company.getContactNumber().matches("\\d{10}")) {
            throw new ValidationException("Contact number must be 10 digits");
        }
    }
}