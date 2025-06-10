// Interface defining the contract for loan-related operations
package bankapp.example.bankapp.service;

// ===== Import entity class =====
import bankapp.example.bankapp.entities.Loans;

import java.util.List; // For returning multiple loan records

// Service interface for managing Loans
public interface LoanService {

    /**
     * Retrieves all loans associated with a given account ID.
     *
     * @param accountId the ID of the account
     * @return list of Loans for that account
     */
    List<Loans> getLoanByAccountId(Integer accountId);

    /**
     * Saves or updates a loan in the database.
     * Can be used for creating a new loan or modifying an existing one.
     *
     * @param loan the loan entity to be saved
     * @return the saved Loans object
     */
    Loans saveLoan(Loans loan);

    /**
     * Retrieves all loans in the system.
     * Useful for admin or dashboard views.
     *
     * @return list of all Loans
     */
    List<Loans> findAll();

    /**
     * Finds a specific loan by its unique ID.
     *
     * @param id the loan's ID
     * @return the matching Loans entity
     */
    Loans findById(Integer id);

    /**
     * Deletes a loan from the system using its ID.
     *
     * @param id the ID of the loan to delete
     */
    void deleteLoanById(Integer id); // ✅ Enables loan deletion

    /**
     * Retrieves all loans that are:
     * - Approved
     * - Have AutoPay enabled
     *
     * Used by the AutoPay scheduler.
     *
     * @return list of approved loans with AutoPay on
     */
    List<Loans> getAllApprovedLoansWithAutoPay();

    /**
     * Retrieves all loans that are approved.
     * Used by the Late Fee Scheduler to check for missed payments.
     *
     * @return list of approved loans
     */
    List<Loans> getAllApprovedLoans(); // ✅ Used for grace/late fee logic
}
