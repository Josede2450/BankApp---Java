// Controller responsible for displaying the user's home page after login
package bankapp.example.bankapp.controller;

// ======= Import statements =======
import bankapp.example.bankapp.entities.Account; // Account entity
import bankapp.example.bankapp.entities.Users;  // Users entity
import bankapp.example.bankapp.security.CustomOidcUser; // Custom OIDC user for OAuth login
import bankapp.example.bankapp.service.AccountService; // Service to handle account logic
import bankapp.example.bankapp.service.UserService;    // Service to handle user logic

import org.springframework.beans.factory.annotation.Autowired; // Used for dependency injection
import org.springframework.security.core.Authentication; // Represents the current authentication token
import org.springframework.security.core.context.SecurityContextHolder; // Holds authentication context
import org.springframework.stereotype.Controller; // Marks this as a controller component
import org.springframework.ui.Model; // Interface for passing data to the view
import org.springframework.web.bind.annotation.GetMapping; // Annotation for GET requests
import org.springframework.web.bind.annotation.RequestMapping; // For mapping URL paths

import java.util.List; // For using List<Account>

// Marks this class as a Spring MVC Controller that returns view templates (e.g., home.html)
@Controller
// All methods inside this controller will be prefixed with "/home"
@RequestMapping("/home")
public class HomeController {

    // Injects AccountService to fetch accounts associated with a user
    @Autowired
    private AccountService accountService;

    // Injects UserService to find user data based on email
    @Autowired
    private UserService userService;

    // Handles GET requests to /home/page
    @GetMapping("/page")
    public String home(Model model) {
        // Get the current authentication object (user session)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the user details (principal) from the authentication object
        Object principal = authentication.getPrincipal();

        // Declare user variable to store the resolved user
        Users user = null;

        // ✅ Case 1: If the user logged in via OAuth2 (Google, etc.)
        if (principal instanceof CustomOidcUser oidcUser) {
            user = oidcUser.getUser(); // Get the Users entity from custom OIDC wrapper

            // ✅ Case 2: If the user logged in using standard Spring Security login
        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            user = userService.findByEmail(springUser.getUsername()); // Lookup by email

            // ⚠️ Case 3: If neither, then it’s an unsupported user type (possibly a system issue)
        } else {
            System.out.println("⚠️ Unknown principal type: " + principal.getClass().getName());
            return "redirect:/login?error=unsupported_user"; // Redirect to login with error
        }

        // Fetch all accounts associated with the authenticated user
        List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());

        // Add account list to the model so Thymeleaf can use it in the HTML page
        model.addAttribute("accounts", accounts);

        // Return the view named "home" → home.html
        return "home";
    }

}
