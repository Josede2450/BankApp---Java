// Implementation of the LateFeeService interface for managing late fee logic
package bankapp.example.bankapp.service;

// ===== Import necessary entity and repository classes =====
import bankapp.example.bankapp.entities.LateFee;
import bankapp.example.bankapp.entities.Loans;
import bankapp.example.bankapp.repository.LateFeeRepository;

import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.stereotype.Service;              // Marks this class as a service bean

import java.math.BigDecimal; // For precise currency calculations

// Marks this class as a Spring-managed service component
@Service
public class LateFeeServiceImpl implements LateFeeService {

    // Injects the LateFeeRepository for database access
    @Autowired
    private LateFeeRepository lateFeeRepository;

    /**
     * Creates and initializes a new LateFee record for a given loan.
     * Called typically when a loan is approved.
     */
    @Override
    public LateFee createLateFee(Loans loan) {
        // Create a new LateFee entity object
        LateFee lateFee = new LateFee();

        // Associate the fee with the loan
        lateFee.setLoan(loan);

        // Start with no grace period (can be incremented later)
        lateFee.setGracePeriodDays(0);

        // Start with no accrued fees
        lateFee.setTotalAccruedFee(BigDecimal.ZERO);

        // Timestamp when late fee logic was last checked (optional)
        lateFee.setLastCheckedAt(null); // can be set by scheduler later

        // Start with no paid fee yet
        lateFee.setTotalPaidFee(BigDecimal.ZERO); // optional tracking

        // ✅ Define a cap for late fees: max 30% of the monthly payment
        if (loan.getMonthlyPayment() != null) {
            // Calculate max late fee as 30% of monthly payment
            BigDecimal maxFee = loan.getMonthlyPayment().multiply(BigDecimal.valueOf(0.30));

            // Set maximum fee allowed for this loan
            lateFee.setMaxFee(maxFee);
        } else {
            // Fallback: zero max fee if monthly payment is not available
            lateFee.setMaxFee(BigDecimal.ZERO);
        }

        // Save the new late fee record to the database
        return lateFeeRepository.save(lateFee);
    }

    /**
     * Saves or updates a LateFee object in the database.
     */
    @Override
    public LateFee save(LateFee fee) {
        // Persist the late fee using JPA repository
        return lateFeeRepository.save(fee);
    }
}
