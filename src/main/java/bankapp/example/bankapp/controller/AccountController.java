// This class handles all HTTP requests related to bank accounts
package bankapp.example.bankapp.controller;

// ===== Import project-specific entities and services =====
import bankapp.example.bankapp.entities.Users;     // User entity
import bankapp.example.bankapp.entities.Account;   // Account entity
import bankapp.example.bankapp.service.AccountService; // Service for account-related logic
import bankapp.example.bankapp.service.UserService;    // Service for user-related logic

// ===== Import Spring components =====
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.security.core.context.SecurityContextHolder; // For getting the logged-in user
import org.springframework.stereotype.Controller; // Marks the class as a Spring MVC controller
import org.springframework.ui.Model; // Interface for adding attributes to the view
import org.springframework.web.bind.annotation.*; // Spring annotations for routing

import java.util.List; // For working with lists

// Marks this class as a Spring MVC Controller
@Controller
// All endpoints in this controller will start with "/accounts"
@RequestMapping("/accounts")
public class AccountController {

    // Injects the AccountService to handle logic for accounts
    @Autowired
    private AccountService accountService;

    // Injects the UserService to retrieve user details
    @Autowired
    private UserService userService;

    // ================================
    // Show Create Account Form
    // ================================
    @GetMapping("/create") // Handles GET request to /accounts/create
    public String showCreateForm(Model model) {
        // Add a new Account object to the model so the form has something to bind to
        model.addAttribute("account", new Account());
        // Return the view name "create-account.html"
        return "create-account";
    }

    // ================================
    // Handle Account Creation
    // ================================
    @PostMapping("/save") // Handles POST request to /accounts/save
    public String saveAccount(@ModelAttribute("account") Account account) {
        // Get the username (email) of the currently authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the user in the database by email
        Users user = userService.findByEmail(username);

        // Link the new account to the logged-in user
        account.setUser(user);

        // Save the account using the account service
        accountService.saveAccount(account);

        // Redirect user to the home page after account creation
        return "redirect:/home/page";
    }

    // ================================
    // Show Edit Account Form
    // ================================
    @GetMapping("/edit/{id}") // Handles GET request to /accounts/edit/{id}
    public String showEditForm(@PathVariable("id") Integer accountId, Model model) {
        // Fetch the account by ID from the database
        Account account = accountService.findById(accountId);

        // Add the account to the model for form pre-population
        model.addAttribute("account", account);

        // Return the edit-account.html view
        return "edit-account";
    }

    // ================================
    // Handle Account Edit Submission
    // ================================
    @PostMapping("/edit/{id}") // Handles POST request to /accounts/edit/{id}
    public String updateAccount(@PathVariable("id") Integer accountId,
                                @ModelAttribute("account") Account updatedAccount) {
        // Fetch the existing account record
        Account existingAccount = accountService.findById(accountId);

        // Update fields from the form input
        existingAccount.setAccountType(updatedAccount.getAccountType());
        existingAccount.setBalance(updatedAccount.getBalance());
        existingAccount.setStatus(updatedAccount.getStatus()); // Allow status updates (e.g., Active, Frozen)

        // Save the updated account
        accountService.saveAccount(existingAccount);

        // Redirect to management dashboard after saving
        return "redirect:/management/dashboard";
    }
}
