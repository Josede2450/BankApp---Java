// Implementation of the PaymentService interface that handles saving and retrieving loan payments
package bankapp.example.bankapp.service;

// ===== Import entity and repository =====
import bankapp.example.bankapp.entities.Payment;               // Entity representing a loan payment
import bankapp.example.bankapp.repository.PaymentRepository;   // Repository for interacting with the payment table

import org.springframework.beans.factory.annotation.Autowired;  // For injecting dependencies
import org.springframework.stereotype.Service;                 // Marks this class as a Spring-managed service

import java.util.List; // For handling lists of payments

// Marks this class as a Spring service bean
@Service
public class PaymentServiceImpl implements PaymentService {

    // Injects the PaymentRepository to access the database
    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Saves a payment to the database.
     * Used when a manual or automatic loan payment is made.
     *
     * @param payment the payment to save
     * @return the saved Payment entity
     */
    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    /**
     * Retrieves all payments made for a specific loan.
     * Used to show a loan's payment history.
     *
     * @param loanId the ID of the loan
     * @return a list of all payments linked to that loan
     */
    @Override
    public List<Payment> getPaymentsByLoanId(Integer loanId) {
        return paymentRepository.findByLoan_LoanId(loanId);
    }
}
