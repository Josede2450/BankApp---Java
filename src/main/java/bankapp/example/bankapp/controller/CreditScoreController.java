// Controller responsible for displaying a user's credit score page
package bankapp.example.bankapp.controller;

// ======= Import service and framework dependencies =======
import bankapp.example.bankapp.service.ExperianCreditService; // Service to fetch credit data from Experian (or mock)
import org.springframework.beans.factory.annotation.Autowired; // Enables dependency injection
import org.springframework.ui.Model; // Interface to pass data to the view
import org.springframework.web.bind.annotation.GetMapping; // Maps GET requests
import org.springframework.stereotype.Controller; // Marks class as a Spring MVC Controller

import java.util.Map; // For holding key-value data (like JSON response from API)

// Marks this class as a Spring Controller
@Controller
public class CreditScoreController {

    // Injects the ExperianCreditService (implementation can call an API or return mock data)
    @Autowired
    private ExperianCreditService experianCreditService;

    // Handles GET request to "/credit-score"
    @GetMapping("/credit-score")
    public String showCreditScore(Model model) {
        // Fetch credit data from Experian (could be FICO, VantageScore, etc.)
        Map<String, Object> creditData = experianCreditService.fetchCreditData();

        // Add credit score and related data to the model to be displayed in the view
        model.addAttribute("creditData", creditData);

        // Return the Thymeleaf view name credit-score.html
        return "credit-score";
    }
}
