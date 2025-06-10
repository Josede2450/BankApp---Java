// Scheduled task class that processes automatic loan payments every minute
package bankapp.example.bankapp.service;

// ===== Import entities and enums =====
import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.LateFee;
import bankapp.example.bankapp.entities.Loans;
import bankapp.example.bankapp.entities.Payment;
import bankapp.example.bankapp.enums.PaymentType;

// ===== Import services and repositories =====
import bankapp.example.bankapp.repository.LateFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled; // Enables scheduling with cron
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

// Marks this class as a Spring-managed component (not a service, but still a bean)
@Component
public class LoanAutoPaymentScheduler {

    // ===== Dependency Injection =====
    @Autowired
    private LoanService loanService; // Service to fetch and update loans

    @Autowired
    private AccountService accountService; // Service to update accounts

    @Autowired
    private PaymentService paymentService; // Service to record payments

    @Autowired
    private LateFeeRepository lateFeeRepository; // Direct access to save/update late fee entries

    /**
     * Scheduled method that runs every minute to process AutoPay loans.
     * Cron expression "0 * * * * ?" means: every minute at second 0.
     */
    @Scheduled(cron = "0 * * * * ?") // Every minute
    public void processAutoPayments() {
        // Fetch all loans that are approved and have AutoPay enabled
        List<Loans> loans = loanService.getAllApprovedLoansWithAutoPay();

        // Get current time without seconds/nanoseconds (used for consistency)
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        // Loop through each eligible loan
        for (Loans loan : loans) {
            // Retrieve account assigned to AutoPay
            Account account = loan.getAutoPayAccount();

            // Monthly loan payment amount
            BigDecimal monthlyPayment = loan.getMonthlyPayment();

            // Get associated late fee record
            LateFee lateFee = loan.getLateFee();

            // Get total late fees accrued (if any)
            BigDecimal accruedFee = (lateFee != null && lateFee.getTotalAccruedFee() != null)
                    ? lateFee.getTotalAccruedFee()
                    : BigDecimal.ZERO;

            // Total amount that needs to be paid: monthly + late fee
            BigDecimal totalPayment = monthlyPayment.add(accruedFee);

            // ✅ Check if the account exists and has enough funds
            if (account == null || account.getBalance().compareTo(totalPayment) < 0) {
                // ❌ Insufficient funds: disable AutoPay and notify
                loan.setAutoPayEnabled(false);
                loan.setAutoPayAccount(null);
                loanService.saveLoan(loan);

                System.out.println("❌ AutoPay disabled for loan #" + loan.getLoanId() + " due to insufficient funds.");
                continue;
            }

            // ✅ Check if a payment was already made this minute
            LocalDateTime lastPaymentTime = loan.getPayments().stream()
                    .filter(p -> p.getPaymentType() == PaymentType.Auto)
                    .map(p -> p.getPaymentTime().toLocalDateTime())
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MIN);

            // Prevent duplicate payments in same minute
            if (lastPaymentTime.plusMinutes(1).isAfter(now)) continue;

            // ✅ Proceed to deduct funds and apply payment
            account.setBalance(account.getBalance().subtract(totalPayment));
            accountService.saveAccount(account);

            // Initialize paid amount if null
            if (loan.getAmountPaid() == null) loan.setAmountPaid(BigDecimal.ZERO);

            // Add this payment to total paid (monthly + late fee)
            loan.setAmountPaid(loan.getAmountPaid().add(monthlyPayment).add(accruedFee));

            // Reduce the remaining balance
            loan.setRemainingAmount(loan.getRemainingAmount().subtract(monthlyPayment));

            // Reduce loan term by 1 month, but not below 0
            loan.setLoanTerm(Math.max(loan.getLoanTerm() - 1, 0));

            // ✅ Handle late fee payment
            if (lateFee != null && accruedFee.compareTo(BigDecimal.ZERO) > 0) {
                // Add to total paid toward fees
                BigDecimal totalPaidFee = lateFee.getTotalPaidFee() != null ? lateFee.getTotalPaidFee() : BigDecimal.ZERO;
                lateFee.setTotalPaidFee(totalPaidFee.add(accruedFee));

                // Reset accrued fee to zero
                lateFee.setTotalAccruedFee(BigDecimal.ZERO);

                // Save updated late fee record
                lateFeeRepository.save(lateFee);
            }

            // ✅ Mark loan as paid if fully completed
            if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0 || loan.getLoanTerm() <= 0) {
                loan.setRemainingAmount(BigDecimal.ZERO);
                loan.setLoanTerm(0);
                loan.setLoanStatus("Paid");
            }

            // Save loan changes
            loanService.saveLoan(loan);

            // ✅ Record the auto-payment as a Payment object
            Payment autoPayment = new Payment();
            autoPayment.setLoan(loan);
            autoPayment.setAccount(account);
            autoPayment.setAmount(totalPayment); // Includes any late fee
            autoPayment.setPaymentType(PaymentType.Auto);
            autoPayment.setPaymentTime(Timestamp.valueOf(now)); // Convert LocalDateTime to SQL Timestamp
            paymentService.save(autoPayment);

            // ✅ Log the successful payment
            System.out.println("✅ Auto-paid loan #" + loan.getLoanId() + " with amount $" + totalPayment + " at " + now);
        }
    }
}
