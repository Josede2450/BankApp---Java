// File: bankapp/example/bankapp/security/CustomAuthenticationFailureHandler.java

package bankapp.example.bankapp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Custom handler for login failures.
 * Determines the reason for failure and redirects back to the login page with a specific error message.
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Called automatically when a login attempt fails.
     *
     * @param request The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param exception The reason authentication failed
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String error;

        // 🔍 Determine the type of exception and create a user-friendly error message
        if (exception instanceof DisabledException) {
            // Case when the user's account exists but is not enabled
            error = "Your account is not enabled. Please contact support.";
        } else if (exception instanceof BadCredentialsException) {
            // Case when the credentials (email or password) are incorrect
            error = "Incorrect email or password.";
        } else {
            // Default case for any other type of authentication error
            error = "Login failed: " + exception.getMessage();
        }

        // 🚪 Redirect the user back to the login page with the error message encoded in the URL
        response.sendRedirect("/login?error=" + URLEncoder.encode(error, "UTF-8"));
    }
}
