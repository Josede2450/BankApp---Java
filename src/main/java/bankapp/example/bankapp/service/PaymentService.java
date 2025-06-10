// Interface defining the contract for handling loan payment operations
package bankapp.example.bankapp.service;

// ===== Import entity class =====
import bankapp.example.bankapp.entities.Payment;

import java.util.List; // For returning multiple payment records

// Service interface for managing Payment-related business logic
public interface PaymentService {

    /**
     * Saves a new payment to the database.
     * Used when a loan payment (manual or auto) is recorded.
     *
     * @param payment the payment object to save
     * @return the saved Payment entity
     */
    Payment save(Payment payment);

    /**
     * Retrieves all payment records for a specific loan.
     * Useful for displaying payment history.
     *
     * @param loanId the ID of the loan
     * @return list of payments associated with the loan
     */
    List<Payment> getPaymentsByLoanId(Integer loanId); // Optional: used in views like loan-payments.html
}
