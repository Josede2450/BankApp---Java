// Implementation of the LoanService interface that handles loan operations
package bankapp.example.bankapp.service;

// ===== Import project entities and repository =====
import bankapp.example.bankapp.entities.Loans;
import bankapp.example.bankapp.repository.LoanRepository;

import org.springframework.beans.factory.annotation.Autowired; // Enables dependency injection
import org.springframework.stereotype.Service;              // Marks this as a Spring service component

import java.util.List; // For handling lists of loans

// Marks this class as a Spring-managed service
@Service
public class LoanServiceImpl implements LoanService {

    // Injects the LoanRepository for database access
    @Autowired
    private LoanRepository loanRepository;

    /**
     * Retrieves all loans linked to a specific account ID.
     * Used in user views or account summaries.
     */
    @Override
    public List<Loans> getLoanByAccountId(Integer accountId) {
        return loanRepository.findByAccount_AccountId(accountId);
    }

    /**
     * Saves a new loan or updates an existing one in the database.
     */
    @Override
    public Loans saveLoan(Loans loan) {
        return loanRepository.save(loan);
    }

    /**
     * Returns all loans in the system.
     * Commonly used in admin dashboards or reports.
     */
    @Override
    public List<Loans> findAll() {
        return loanRepository.findAll();
    }

    /**
     * Finds a specific loan by its ID.
     * Returns null if not found.
     */
    @Override
    public Loans findById(Integer id) {
        return loanRepository.findById(id).orElse(null);
    }

    /**
     * Deletes a loan by its ID.
     * Used in loan management workflows.
     */
    @Override
    public void deleteLoanById(Integer id) {
        loanRepository.deleteById(id);
    }

    /**
     * Retrieves all loans that are:
     * - Approved
     * - Have AutoPay enabled
     * Used by the auto-payment scheduler.
     */
    @Override
    public List<Loans> getAllApprovedLoansWithAutoPay() {
        return loanRepository.findAllApprovedWithAutoPay();
    }

    /**
     * Retrieves all loans that are approved,
     * regardless of whether AutoPay is enabled.
     * Used by the late fee scheduler.
     */
    @Override
    public List<Loans> getAllApprovedLoans() {
        return loanRepository.findAllApprovedLoans();
    }
}
