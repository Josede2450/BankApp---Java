package bankapp.example.bankapp.controller; // Declares the package for this controller class

import org.springframework.stereotype.Controller; // Imports the Controller annotation
import org.springframework.web.bind.annotation.GetMapping; // Imports annotation to handle HTTP GET requests

@Controller // Indicates that this class is a Spring MVC controller
public class LoginController {

    // Handles HTTP GET requests to the "/login" endpoint
    @GetMapping("/login")
    public String showLoginPage() {
        // Returns the name of the Thymeleaf template ("login.html") to be rendered
        return "login";
    }
}
