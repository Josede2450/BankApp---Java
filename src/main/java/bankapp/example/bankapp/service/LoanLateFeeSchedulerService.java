// Service responsible for periodically applying late fees to overdue loan payments
package bankapp.example.bankapp.service;

// ===== Import entities and enums =====
import bankapp.example.bankapp.entities.Loans;
import bankapp.example.bankapp.entities.LateFee;
import bankapp.example.bankapp.entities.Payment;
import bankapp.example.bankapp.enums.PaymentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;   // Enables cron-based scheduling
import org.springframework.stereotype.Service;           // Marks this class as a service component
import org.springframework.transaction.annotation.Transactional; // Ensures DB consistency across operations

import java.math.BigDecimal;              // Used for currency-accurate calculations
import java.sql.Timestamp;               // For storing precise update time
import java.time.LocalDateTime;          // For timestamping
import java.util.Comparator;             // For finding most recent payment
import java.util.List;                   // For working with lists

// Marks this class as a Spring-managed service
@Service
public class LoanLateFeeSchedulerService {

    // ===== Inject required services =====
    @Autowired
    private LoanService loanService;          // Fetches loans to evaluate for late fees

    @Autowired
    private LateFeeService lateFeeService;    // Updates late fee records

    /**
     * Scheduled method to evaluate and apply late fees.
     * Runs every minute based on cron: second=0, every minute.
     */
    @Transactional // Ensures all DB updates happen atomically
    @Scheduled(cron = "0 * * * * ?") // Every minute
    public void applyLateFees() {
        // Get all loans that are approved and potentially overdue
        List<Loans> loans = loanService.getAllApprovedLoans();

        // Loop through each loan
        for (Loans loan : loans) {
            // Get the associated late fee tracker
            LateFee fee = loan.getLateFee();

            // Skip loans without a late fee setup
            if (fee == null) continue;

            // Find the latest manual or auto payment time
            LocalDateTime lastPaymentTime = loan.getPayments().stream()
                    .filter(p -> p.getPaymentType() == PaymentType.Manual || p.getPaymentType() == PaymentType.Auto)
                    .map(p -> p.getPaymentTime().toLocalDateTime())
                    .max(Comparator.naturalOrder())
                    .orElse(loan.getCreatedAt().toLocalDateTime()); // Default to loan creation date if no payments

            // Get the current time with seconds and nanoseconds cleared
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

            // ✅ Skip applying late fee if last payment was made within the last 1 minute
            if (lastPaymentTime.plusMinutes(1).isAfter(now)) continue;

            // Get current grace period day count
            int grace = fee.getGracePeriodDays();

            // If within grace period (first 3 missed cycles), just increment it
            if (grace < 3) {
                fee.setGracePeriodDays(grace + 1);
            } else {
                // ✅ Calculate the new fee: 10% of monthly payment
                BigDecimal newFee = loan.getMonthlyPayment().multiply(BigDecimal.valueOf(0.10));

                // Get current accrued fee amount
                BigDecimal currentTotal = fee.getTotalAccruedFee();

                // Add the new fee to the total
                BigDecimal cappedTotal = currentTotal.add(newFee);

                // ✅ Ensure it doesn't exceed the defined max fee
                BigDecimal maxFee = fee.getMaxFee() != null ? fee.getMaxFee() : BigDecimal.valueOf(Double.MAX_VALUE);

                // Cap if over the limit
                if (cappedTotal.compareTo(maxFee) > 0) {
                    cappedTotal = maxFee;
                }

                // Apply the final (possibly capped) total
                fee.setTotalAccruedFee(cappedTotal);

                // Reset the grace period after applying fee
                fee.setGracePeriodDays(0);
            }

            // Update the last-checked timestamp
            fee.setLastCheckedAt(Timestamp.valueOf(now));

            // Save changes to the late fee record
            lateFeeService.save(fee);
        }
    }
}
