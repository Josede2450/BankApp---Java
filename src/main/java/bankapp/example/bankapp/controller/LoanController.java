package bankapp.example.bankapp.controller;

// ===== Import required classes and packages =====
import bankapp.example.bankapp.entities.*;
import bankapp.example.bankapp.enums.PaymentType;
import bankapp.example.bankapp.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

// Marks this class as a Spring MVC Controller
@Controller
@RequestMapping("/loans") // All URLs in this controller will start with /loans
public class LoanController {

    // ====== Service Layer Injections ======
    @Autowired private UserService userService; // Handles user-related operations
    @Autowired private LoanService loanService; // Handles loan logic
    @Autowired private AccountService accountService; // For account access and updates
    @Autowired private LateFeeService lateFeeService; // Manages late fees on loans
    @Autowired private PaymentService paymentService; // Handles recording and retrieving payments

    // ====== Show all loans for current user ======
    @GetMapping("/page")
    public String loan(Model model) {
        // Get current user's email from security context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the user entity from DB
        Users user = userService.findByEmail(email);

        // Get list of accounts associated with this user
        List<Account> accounts = user.getAccounts();

        // If user has no account, display error
        if (accounts == null || accounts.isEmpty()) {
            model.addAttribute("error", "No account found for this user.");
            return "loans";
        }

        // Use the first account to find associated loans
        Integer accountId = accounts.get(0).getAccountId();
        List<Loans> loans = loanService.getLoanByAccountId(accountId);

        // Add loans to the view model
        model.addAttribute("loans", loans);
        return "loans"; // Returns loans.html page
    }

    // ====== Show loan creation form ======
    @GetMapping("/create")
    public String showLoanForm(Model model) {
        model.addAttribute("loan", new Loans()); // Empty loan for form binding
        return "loan-create"; // Show loan-create.html
    }

    // ====== Handle loan form submission ======
    @PostMapping("/save")
    public String saveLoan(@ModelAttribute("loan") Loans loan, RedirectAttributes redirectAttributes) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);

        // Ensure user has an account
        if (user == null || user.getAccounts().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No account found. Please create an account first.");
            return "redirect:/loans/create";
        }

        // Associate loan with user's account and mark as pending
        Account account = user.getAccounts().get(0);
        loan.setAccount(account);
        loan.setLoanStatus("Pending");

        // Save to DB
        loanService.saveLoan(loan);
        redirectAttributes.addFlashAttribute("success", "Loan request submitted successfully.");
        return "redirect:/loans/page";
    }

    // ====== Show edit form for a loan ======
    @GetMapping("/edit/{id}")
    public String showEditLoanForm(@PathVariable("id") Integer loanId, Model model, RedirectAttributes redirectAttributes) {
        Loans loan = loanService.findById(loanId);
        if (loan == null) {
            redirectAttributes.addFlashAttribute("error", "Loan not found.");
            return "redirect:/management/dashboard";
        }

        Users user = loan.getAccount().getUser();
        List<Account> userAccounts = user.getAccounts();
        List<Payment> loanPayments = loan.getPayments(); // Fetch payments

        model.addAttribute("loan", loan);
        model.addAttribute("accounts", userAccounts);
        model.addAttribute("payments", loanPayments);
        return "edit-loan"; // Returns edit-loan.html
    }

    // ====== Handle loan update ======
    @PostMapping("/edit/{id}")
    public String updateLoan(@PathVariable("id") Integer loanId,
                             @ModelAttribute("loan") Loans updatedLoan,
                             RedirectAttributes redirectAttributes) {
        Loans existingLoan = loanService.findById(loanId);
        if (existingLoan == null) {
            redirectAttributes.addFlashAttribute("error", "Loan not found.");
            return "redirect:/management/dashboard";
        }

        String currentStatus = existingLoan.getLoanStatus();
        String newStatus = updatedLoan.getLoanStatus();

        // Status can't be changed once approved/rejected
        if (!"Pending".equalsIgnoreCase(currentStatus) && !currentStatus.equalsIgnoreCase(newStatus)) {
            redirectAttributes.addFlashAttribute("error", "Cannot change loan status once it has been approved or rejected.");
            return "redirect:/management/dashboard";
        }

        boolean wasPending = "Pending".equalsIgnoreCase(currentStatus);
        boolean isNowApproved = "Approved".equalsIgnoreCase(newStatus);

        Account selectedAccount = accountService.findById(updatedLoan.getAccount().getAccountId());
        if (selectedAccount == null) {
            redirectAttributes.addFlashAttribute("error", "Account not found.");
            return "redirect:/management/dashboard";
        }

        existingLoan.setAccount(selectedAccount);
        existingLoan.setLoanType(updatedLoan.getLoanType());
        existingLoan.setLoanStatus(newStatus);
        existingLoan.setAmount(updatedLoan.getAmount());
        existingLoan.setInterestRate(updatedLoan.getInterestRate());
        existingLoan.setLoanTerm(updatedLoan.getLoanTerm());

        // ===== Amortized Payment Calculation =====
        BigDecimal principal = updatedLoan.getAmount();
        BigDecimal annualRate = updatedLoan.getInterestRate();
        int months = updatedLoan.getLoanTerm();

        if (annualRate.compareTo(BigDecimal.ZERO) > 0 && months > 0) {
            BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
            BigDecimal onePlusRPowerN = BigDecimal.ONE.add(monthlyRate).pow(months);
            BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
            BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
            BigDecimal monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
            BigDecimal totalRepayment = monthlyPayment.multiply(BigDecimal.valueOf(months));

            existingLoan.setMonthlyPayment(monthlyPayment);
            existingLoan.setRemainingAmount(totalRepayment);
            existingLoan.setExpectedTotal(totalRepayment);
        } else {
            existingLoan.setMonthlyPayment(BigDecimal.ZERO);
            existingLoan.setRemainingAmount(principal);
            existingLoan.setExpectedTotal(principal);
        }

        // ===== On Approval, Credit Funds =====
        if (wasPending && isNowApproved) {
            selectedAccount.setBalance(selectedAccount.getBalance().add(principal));
            accountService.saveAccount(selectedAccount);
            lateFeeService.createLateFee(existingLoan);
        }

        loanService.saveLoan(existingLoan);
        redirectAttributes.addFlashAttribute("success", "Loan updated successfully.");
        return "redirect:/management/dashboard";
    }

    // ====== Delete a loan ======
    @PostMapping("/delete/{id}")
    public String deleteLoan(@PathVariable("id") Integer loanId, RedirectAttributes redirectAttributes) {
        loanService.deleteLoanById(loanId);
        redirectAttributes.addFlashAttribute("success", "Loan deleted successfully.");
        return "redirect:/management/dashboard";
    }

    // ====== Show payment form for a loan ======
    @GetMapping("/pay/{id}")
    public String showMyLoanInformation(@PathVariable("id") Integer loanId, Model model) {
        Loans loan = loanService.findById(loanId);

        if (loan == null || !"Approved".equalsIgnoreCase(loan.getLoanStatus())) {
            model.addAttribute("error", "Loan not found or not eligible for payment.");
            return "loan-payment-preview";
        }

        List<Account> accounts = accountService.getAccountsByUserId(loan.getAccount().getUser().getUserId());

        model.addAttribute("loan", loan);
        model.addAttribute("accounts", accounts);
        model.addAttribute("paymentAmount", loan.getMonthlyPayment());
        return "loan-payment-preview";
    }

    // ====== Process manual loan payment ======
    @PostMapping("/pay/{id}")
    public String makePayment(@PathVariable("id") Integer loanId,
                              @RequestParam("paymentType") String paymentType,
                              @RequestParam("accountId") Integer accountId,
                              RedirectAttributes redirectAttributes) {

        Loans loan = loanService.findById(loanId);
        Account payerAccount = accountService.findById(accountId);

        // ✅ Handle missing loan/account or invalid loan status
        if (loan == null || payerAccount == null || !"Approved".equalsIgnoreCase(loan.getLoanStatus())) {
            redirectAttributes.addFlashAttribute("error", "Loan or payment account not found or not eligible.");
            return "redirect:/loans/page";
        }

        BigDecimal monthly = loan.getMonthlyPayment();
        BigDecimal lateFee = loan.getLateFee() != null ? loan.getLateFee().getTotalAccruedFee() : BigDecimal.ZERO;

        BigDecimal paymentAmount = "full".equalsIgnoreCase(paymentType)
                ? loan.getRemainingAmount().add(lateFee)
                : monthly.add(lateFee);

        // ✅ Handle insufficient funds
        if (payerAccount.getBalance().compareTo(paymentAmount) < 0) {
            redirectAttributes.addFlashAttribute("error", "❌ Insufficient funds in selected account.");
            return "redirect:/loans/page";
        }

        // Deduct payment
        payerAccount.setBalance(payerAccount.getBalance().subtract(paymentAmount));
        accountService.saveAccount(payerAccount);

        if (loan.getAmountPaid() == null) loan.setAmountPaid(BigDecimal.ZERO);

        if ("full".equalsIgnoreCase(paymentType)) {
            loan.setAmountPaid(loan.getAmountPaid().add(loan.getRemainingAmount()).add(lateFee));
            loan.setRemainingAmount(BigDecimal.ZERO);
            loan.setLoanTerm(0);
            loan.setLoanStatus("Paid");
        } else {
            loan.setAmountPaid(loan.getAmountPaid().add(monthly).add(lateFee));
            BigDecimal newRemaining = loan.getRemainingAmount().subtract(monthly);
            loan.setRemainingAmount(newRemaining);
            int newTerm = loan.getLoanTerm() - 1;
            loan.setLoanTerm(Math.max(newTerm, 0));

            if (newRemaining.compareTo(BigDecimal.ZERO) <= 0 || newTerm <= 0) {
                loan.setRemainingAmount(BigDecimal.ZERO);
                loan.setLoanTerm(0);
                loan.setLoanStatus("Paid");
            }
        }

        // Handle late fee
        if (loan.getLateFee() != null) {
            LateFee fee = loan.getLateFee();
            if (fee.getTotalPaidFee() == null) fee.setTotalPaidFee(BigDecimal.ZERO);
            fee.setTotalPaidFee(fee.getTotalPaidFee().add(lateFee));
            fee.setTotalAccruedFee(BigDecimal.ZERO);
            fee.setGracePeriodDays(0);
            lateFeeService.save(fee);
        }

        // Record payment
        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setAccount(payerAccount);
        payment.setAmount(paymentAmount);
        payment.setPaymentType(PaymentType.Manual);
        payment.setPaymentTime(new Timestamp(System.currentTimeMillis()));
        paymentService.save(payment);

        loanService.saveLoan(loan);

        // ✅ Optionally, add a success message too
        redirectAttributes.addFlashAttribute("success", "✅ Payment completed successfully.");

        return "redirect:/loans/page";
    }


    // ====== Toggle AutoPay ON/OFF ======
    @PostMapping("/autopay/{id}")
    public String toggleAutoPay(@PathVariable("id") Integer loanId,
                                @RequestParam("enable") boolean enable,
                                @RequestParam(value = "accountId", required = false) Integer accountId,
                                RedirectAttributes redirectAttributes) {
        Loans loan = loanService.findById(loanId);
        if (loan == null || !"Approved".equalsIgnoreCase(loan.getLoanStatus())) {
            redirectAttributes.addFlashAttribute("error", "Invalid loan.");
            return "redirect:/loans/page";
        }

        loan.setAutoPayEnabled(enable);
        if (enable && accountId != null) {
            Account selectedAccount = accountService.findById(accountId);
            loan.setAutoPayAccount(selectedAccount);
        } else if (!enable) {
            loan.setAutoPayAccount(null);
        }

        loanService.saveLoan(loan);
        redirectAttributes.addFlashAttribute("success",
                enable ? "AutoPay enabled for Loan #" + loanId : "AutoPay disabled for Loan #" + loanId);
        return "redirect:/loans/page";
    }

    // ====== View all payments made for a loan ======
    @GetMapping("/payments/{loanId}")
    public String viewPayments(@PathVariable("loanId") Integer loanId, Model model) {
        Loans loan = loanService.findById(loanId);
        List<Payment> payments = loan.getPayments();
        model.addAttribute("loan", loan);
        model.addAttribute("payments", payments);
        return "loan-payments";
    }
}
