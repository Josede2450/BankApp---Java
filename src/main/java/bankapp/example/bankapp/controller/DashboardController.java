// Controller for handling admin/manager dashboard operations
package bankapp.example.bankapp.controller;

// ======= Import application entity classes =======
import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.Loans;
import bankapp.example.bankapp.entities.Transactions;
import bankapp.example.bankapp.entities.Users;

// ======= Import application service classes =======
import bankapp.example.bankapp.service.AccountService;
import bankapp.example.bankapp.service.LoanService;
import bankapp.example.bankapp.service.TransactionService;
import bankapp.example.bankapp.service.UserService;

// ======= Import Spring Framework components =======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // Used to restrict access based on user roles
import org.springframework.stereotype.Controller; // Marks this class as a Spring MVC Controller
import org.springframework.ui.Model; // Interface for adding attributes to the view
import org.springframework.web.bind.annotation.GetMapping; // Handles GET requests
import org.springframework.web.bind.annotation.RequestMapping; // Maps controller-level base paths

import java.util.List; // List interface for collections

// Marks this class as a controller and maps all endpoints to start with /management
@Controller
@RequestMapping("/management")
public class DashboardController {

    // Injects service for accessing account data
    @Autowired
    private AccountService accountService;

    // Injects service for loan data handling
    @Autowired
    private LoanService loanService;

    // Injects service for transaction data
    @Autowired
    private TransactionService transactionService;

    // Injects user service for retrieving user info
    @Autowired
    private UserService userService;

    // =============================
    // Display Admin Dashboard Page
    // =============================
    @GetMapping("/dashboard") // Maps GET request to /management/dashboard
    @PreAuthorize("hasRole('Admin')") // Restricts access to users with ROLE_Admin only
    public String showMyDashboard(Model model) {

        // Retrieve all users in the system
        List<Users> users = userService.findAll();

        // Retrieve all accounts
        List<Account> accounts = accountService.findAll();

        // Retrieve all loans
        List<Loans> loans = loanService.findAll();

        // Retrieve all transactions
        List<Transactions> transactions = transactionService.findAll();

        // Add data to the model so it can be displayed in dashboard.html
        model.addAttribute("users", users);                 // Add user list to model
        model.addAttribute("accounts", accounts);           // Add account list to model
        model.addAttribute("transactions", transactions);   // Add transaction list to model
        model.addAttribute("loans", loans);                 // Add loan list to model

        // Return the view name "dashboard" → resolves to dashboard.html
        return "dashboard";
    }
}
