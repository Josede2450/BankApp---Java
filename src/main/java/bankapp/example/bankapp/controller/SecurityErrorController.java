package bankapp.example.bankapp.controller; // Declares the package for the controller class

// Spring MVC annotations and classes for building the controller
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Marks this class as a Spring MVC controller that returns view templates
public class SecurityErrorController {

    // Handles GET requests to "/access-denied"
    @GetMapping("/access-denied")
    public String accessDeniedPage(Model model) {
        // Add an attribute to the model with a custom access denied message
        model.addAttribute("message", "You are not authorized to access this page.");

        // Returns the Thymeleaf view name "access-denied.html"
        return "access-denied";
    }
}
