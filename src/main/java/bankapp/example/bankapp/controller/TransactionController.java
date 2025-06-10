package bankapp.example.bankapp.controller; // Package declaration

// Importing necessary classes
import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.Transactions;
import bankapp.example.bankapp.entities.Users;
import bankapp.example.bankapp.service.AccountService;
import bankapp.example.bankapp.service.TransactionService;
import bankapp.example.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller // Marks this class as a Spring MVC Controller
@RequestMapping("/transactions") // Base URL mapping for this controller
public class TransactionController {

    @Autowired // Inject UserService to access user-related operations
    private UserService userService;

    @Autowired // Inject AccountService to handle account operations
    private AccountService accountService;

    @Autowired // Inject TransactionService to manage transactions
    private TransactionService transactionService;

    // Endpoint to load the transaction page and user's account data
    @GetMapping("/initiate")
    public String home(Model model) {
        // Get currently logged-in user's email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch user details using email
        Users user = userService.findByEmail(email);

        // Get list of transactions for this user
        List<Transactions> transactions = transactionService.getTransactionsByUserId(user.getUserId());
        model.addAttribute("transactions", transactions); // Add to model for view

        // Get user's accounts
        List<Account> userAccounts = accountService.getAccountsByUserId(user.getUserId());
        model.addAttribute("userAccounts", userAccounts); // Add to model for view

        return "transactions"; // Return view name
    }

    // Search users and refresh transaction page with results
    @GetMapping("/search")
    public String searchUsers(@RequestParam("query") String query, Model model) {
        List<Users> users = userService.searchUsers(query); // Find users by query
        model.addAttribute("users", users); // Add result to model

        // Get currently logged-in user's details again
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);

        // Add transactions and accounts again
        List<Transactions> transactions = transactionService.getTransactionsByUserId(user.getUserId());
        model.addAttribute("transactions", transactions);

        List<Account> userAccounts = accountService.getAccountsByUserId(user.getUserId());
        model.addAttribute("userAccounts", userAccounts);

        return "transactions"; // Return updated page
    }

    // Handle money transfers between users
    @PostMapping("/send")
    public String sendTransaction(@RequestParam("recipientId") Integer recipientId,
                                  @RequestParam("senderAccountId") Integer senderAccountId,
                                  @RequestParam("amount") Double amount,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Get sender information from session
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Users sender = userService.findByEmail(senderEmail);

        // Find recipient by ID
        Users recipient = userService.findById(recipientId);

        // Prevent self-transfer
        if (sender.getUserId().equals(recipient.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You cannot send money to yourself.");
            return "redirect:/transactions/initiate";
        }

        // Validate input
        if (sender == null || recipient == null || amount == null || amount <= 0) {
            redirectAttributes.addFlashAttribute("error", "Invalid transaction request.");
            return "redirect:/transactions/initiate";
        }

        // Validate sender's account
        Account senderAccount = accountService.findById(senderAccountId);
        if (senderAccount == null || !senderAccount.getUser().getUserId().equals(sender.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or unauthorized account selected.");
            return "redirect:/transactions/initiate";
        }

        // Get recipient's primary account
        Account recipientAccount = accountService.getPrimaryAccountByUserId(recipient.getUserId());
        if (recipientAccount == null || senderAccount.getBalance() == null) {
            redirectAttributes.addFlashAttribute("error", "Account issue.");
            return "redirect:/home/page";
        }

        // Check sender has enough funds
        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        if (senderAccount.getBalance().compareTo(transferAmount) < 0) {
            redirectAttributes.addFlashAttribute("error", "Insufficient balance.");
            return "redirect:/home/page";
        }

        // Perform the fund transfer logic
        transactionService.transferFunds(senderAccount, recipientAccount, amount);

        // Create sender transaction record
        Transactions senderTxn = new Transactions();
        senderTxn.setAccount(senderAccount);
        senderTxn.setTransactionType("Transfer Out");
        senderTxn.setAmount(transferAmount);
        senderTxn.setDescription("Sent to: " + recipient.getFirstName() + " " + recipient.getLastName());
        transactionService.saveTransaction(senderTxn); // Save sender record

        // Create recipient transaction record
        Transactions recipientTxn = new Transactions();
        recipientTxn.setAccount(recipientAccount);
        recipientTxn.setTransactionType("Transfer In");
        recipientTxn.setAmount(transferAmount);
        recipientTxn.setDescription("Received from: " + sender.getFirstName() + " " + sender.getLastName());
        recipientTxn.setLinkedTransactionId(senderTxn.getTransactionId()); // Link to sender transaction
        transactionService.saveTransaction(recipientTxn); // Save recipient record

        // Link back recipient to sender
        senderTxn.setLinkedTransactionId(recipientTxn.getTransactionId());
        transactionService.saveTransaction(senderTxn); // Update sender record with link

        // Notify success
        redirectAttributes.addFlashAttribute("success", "Transaction completed successfully.");
        return "redirect:/home/page"; // Redirect to dashboard
    }

    // Load edit form for a specific transaction
    @GetMapping("/edit/{id}")
    public String showEditTransactionForm(@PathVariable("id") Integer transactionId, Model model) {
        Transactions transaction = transactionService.findById(transactionId); // Find transaction by ID
        model.addAttribute("transaction", transaction); // Add to model
        return "edit-transaction"; // Show edit form
    }

    // Handle transaction update
    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable("id") Integer transactionId,
                                    @ModelAttribute("transaction") Transactions updatedTxn) {

        Transactions existingTxn = transactionService.findById(transactionId); // Load original transaction
        Account account = existingTxn.getAccount(); // Get related account

        // Calculate amount difference
        BigDecimal oldAmount = existingTxn.getAmount();
        BigDecimal newAmount = updatedTxn.getAmount();
        BigDecimal difference = newAmount.subtract(oldAmount);

        // Adjust account balance based on transaction type
        if (existingTxn.getTransactionType().equalsIgnoreCase("Transfer In")) {
            account.setBalance(account.getBalance().add(difference));
        } else if (existingTxn.getTransactionType().equalsIgnoreCase("Transfer Out")) {
            account.setBalance(account.getBalance().subtract(difference));
        }

        // Update and save transaction
        existingTxn.setAmount(newAmount);
        accountService.saveAccount(account);
        transactionService.saveTransaction(existingTxn);

        // Update linked transaction if exists
        if (existingTxn.getLinkedTransactionId() != null) {
            Transactions linkedTxn = transactionService.findById(existingTxn.getLinkedTransactionId());
            Account linkedAccount = linkedTxn.getAccount();

            // Update linked account's balance
            if (linkedTxn.getTransactionType().equalsIgnoreCase("Transfer In")) {
                linkedAccount.setBalance(linkedAccount.getBalance().add(difference));
            } else if (linkedTxn.getTransactionType().equalsIgnoreCase("Transfer Out")) {
                linkedAccount.setBalance(linkedAccount.getBalance().subtract(difference));
            }

            linkedTxn.setAmount(newAmount);
            accountService.saveAccount(linkedAccount);
            transactionService.saveTransaction(linkedTxn);
        }

        return "redirect:/management/dashboard"; // Redirect after update
    }

    // Delete a transaction and adjust balance
    @PostMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable("id") Integer transactionId) {
        Transactions txn = transactionService.findById(transactionId); // Get transaction

        if (txn == null) {
            return "redirect:/management/dashboard"; // Skip if not found
        }

        Account account = txn.getAccount(); // Get account
        BigDecimal amount = txn.getAmount(); // Amount to reverse

        // Reverse balance for main transaction
        if (txn.getTransactionType().equalsIgnoreCase("Transfer In")) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if (txn.getTransactionType().equalsIgnoreCase("Transfer Out")) {
            account.setBalance(account.getBalance().add(amount));
        }

        // Reverse and delete linked transaction if exists
        if (txn.getLinkedTransactionId() != null) {
            Transactions linkedTxn = transactionService.findById(txn.getLinkedTransactionId());

            if (linkedTxn != null) {
                Account linkedAccount = linkedTxn.getAccount();

                if (linkedTxn.getTransactionType().equalsIgnoreCase("Transfer In")) {
                    linkedAccount.setBalance(linkedAccount.getBalance().subtract(amount));
                } else if (linkedTxn.getTransactionType().equalsIgnoreCase("Transfer Out")) {
                    linkedAccount.setBalance(linkedAccount.getBalance().add(amount));
                }

                transactionService.deleteTransactionById(linkedTxn.getTransactionId()); // Delete linked
                accountService.saveAccount(linkedAccount); // Save linked account update
            }
        }

        transactionService.deleteTransactionById(transactionId); // Delete main transaction
        accountService.saveAccount(account); // Save account balance

        return "redirect:/management/dashboard"; // Redirect after deletion
    }
}
