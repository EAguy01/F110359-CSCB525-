package com.homemanager.service;

import com.homemanager.dao.PaymentDAO;
import com.homemanager.dao.CompanyDAO;
import com.homemanager.model.Payment;
import com.homemanager.util.ValidationException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentService {
    private PaymentDAO paymentDAO;
    private CompanyDAO companyDAO;
    private FeeService feeService;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.companyDAO = new CompanyDAO();
        this.feeService = new FeeService();
    }
    public FeeService getFeeService() {
        return feeService;
    }
    public void createPayment(Payment payment) throws ValidationException, SQLException {
        validatePayment(payment);

        // Calculate the expected amount
        BigDecimal expectedAmount = feeService.calculateMonthlyFee(payment.getApartmentId());

        // Validate payment amount
        if (payment.getAmount().compareTo(expectedAmount) != 0) {
            throw new ValidationException("Payment amount does not match the calculated fee");
        }

        paymentDAO.create(payment);
        updateCompanyRevenue(payment);
        recordPaymentToFile(payment);
    }
// za sega ne se izpozlva

    public Payment getPaymentById(Integer id) throws SQLException {
        return paymentDAO.findById(id);
    }
// za sega ne se izpozlva

    public List<Payment> getPaymentsByApartment(Integer apartmentId) throws SQLException {
        return paymentDAO.findByApartment(apartmentId);
    }
// za sega ne se izpozlva

    public List<Payment> getPaymentsByApartmentAndDateRange(Integer apartmentId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        return paymentDAO.findByApartmentAndDateRange(apartmentId, startDate, endDate);
    }

    public void updatePaymentStatus(Integer paymentId, String status) throws SQLException {
        Payment payment = paymentDAO.findById(paymentId);
        if (payment != null) {
            payment.setStatus(status);
            paymentDAO.update(payment);
        }
    }
// za sega ne se izpozlva

    public BigDecimal calculateTotalPaymentsForCompany(Integer companyId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        return paymentDAO.calculateTotalPaymentsForCompany(companyId, startDate, endDate);
    }
// za sega ne se izpozlva

    public BigDecimal calculateTotalPaymentsForBuilding(Integer buildingId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        return paymentDAO.calculateTotalPaymentsForBuilding(buildingId, startDate, endDate);
    }

    public void checkOverduePayments() throws SQLException {
        LocalDate today = LocalDate.now();
        List<Payment> pendingPayments = paymentDAO.findPendingPayments();

        for (Payment payment : pendingPayments) {
            if (payment.getPaymentMonth().plusMonths(1).isBefore(today)) {
                payment.setStatus("OVERDUE");
                paymentDAO.update(payment);
            }
        }
    }

    private void validatePayment(Payment payment) throws ValidationException {
        if (payment.getAmount() == null || payment.getAmount().signum() <= 0) {
            throw new ValidationException("Invalid payment amount");
        }
        if (payment.getPaymentDate() == null) {
            throw new ValidationException("Payment date cannot be null");
        }
        if (payment.getPaymentMonth() == null) {
            throw new ValidationException("Payment month cannot be null");
        }
        if (payment.getPaymentMonth().isAfter(LocalDate.now())) {
            throw new ValidationException("Payment month cannot be in the future");
        }
    }

    private void updateCompanyRevenue(Payment payment) throws SQLException {
        // Get company ID from apartment -> building -> employee -> company chain
        Integer companyId = paymentDAO.getCompanyIdForPayment(payment.getApartmentId());
        if (companyId != null) {
            companyDAO.updateRevenue(companyId, payment.getAmount());
        }
    }

    private void recordPaymentToFile(Payment payment) throws SQLException {
        String fileName = "payment_records.txt";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            // Get all relevant information
            String paymentInfo = String.format(
                    "Payment ID: %d\n" +
                            "Company: %s\n" +
                            "Employee: %s\n" +
                            "Building: %s\n" +
                            "Apartment: %s\n" +
                            "Amount: %.2f\n" +
                            "Date Paid: %s\n" +
                            "Payment Month: %s\n" +
                            "Status: %s\n" +
                            "Recorded at: %s\n" +
                            "----------------------------------------\n",
                    payment.getPaymentId(),
                    paymentDAO.getCompanyNameForPayment(payment.getApartmentId()),
                    paymentDAO.getEmployeeNameForPayment(payment.getApartmentId()),
                    paymentDAO.getBuildingAddressForPayment(payment.getApartmentId()),
                    paymentDAO.getApartmentNumberForPayment(payment.getApartmentId()),
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getPaymentMonth(),
                    payment.getStatus(),
                    LocalDate.now().format(formatter)
            );

            writer.println(paymentInfo);
        } catch (IOException e) {
            throw new SQLException("Error recording payment to file: " + e.getMessage());
        }
    }

    public List<Payment> generatePaymentReport(Integer companyId, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        List<Payment> payments = paymentDAO.findByCompanyAndDateRange(companyId, startDate, endDate);

        // Calculate statistics
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalPayments = payments.size();
        int overduePayments = 0;

        for (Payment payment : payments) {
            totalAmount = totalAmount.add(payment.getAmount());
            if ("OVERDUE".equals(payment.getStatus())) {
                overduePayments++;
            }
        }

        return payments;
    }

    public void exportPaymentsToCSV(List<Payment> payments, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            writer.println("Payment ID,Apartment,Amount,Payment Date,Payment Month,Status");


            for (Payment payment : payments) {
                writer.printf("%d,%s,%.2f,%s,%s,%s\n",
                        payment.getPaymentId(),
                        payment.getApartmentId(),
                        payment.getAmount(),
                        payment.getPaymentDate(),
                        payment.getPaymentMonth(),
                        payment.getStatus()
                );
            }
        }
    }
}