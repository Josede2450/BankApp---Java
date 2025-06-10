// This interface defines the contract for handling loan late fees
package bankapp.example.bankapp.service;

// ===== Import required entity classes =====
import bankapp.example.bankapp.entities.LateFee; // Entity representing late fee details
import bankapp.example.bankapp.entities.Loans;   // Entity representing the loan linked to the fee

// Service interface for late fee-related operations
public interface LateFeeService {

    /**
     * Creates and initializes a new LateFee entry for a given loan.
     * Typically used when a loan is first approved.
     *
     * @param loan the loan for which the late fee is being created
     * @return the saved LateFee entity
     */
    LateFee createLateFee(Loans loan);

    /**
     * Saves or updates a LateFee entity in the database.
     * Can be used when modifying grace period, paid amount, or accrued fees.
     *
     * @param fee the LateFee entity to persist
     * @return the saved LateFee object
     */
    LateFee save(LateFee fee); // ✅ Explicit save method for updating late fees
}
