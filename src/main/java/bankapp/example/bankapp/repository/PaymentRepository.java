package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Payment entities.
 * Extends JpaRepository to provide CRUD and custom query methods.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    /**
     * Retrieves all payment records associated with a specific loan.
     * Traverses the entity relationship: Payment → Loan
     *
     * @param loanId the ID of the loan
     * @return a list of payments related to that loan
     */
    List<Payment> findByLoan_LoanId(Integer loanId);
}
