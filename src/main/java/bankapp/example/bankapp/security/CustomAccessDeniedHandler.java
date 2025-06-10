package bankapp.example.bankapp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom handler for 403 Access Denied errors.
 * This is triggered when an authenticated user tries to access a resource they don't have permission for.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles access denied exceptions by forwarding the user to a custom error page.
     *
     * @param request The HTTP request that caused the error
     * @param response The HTTP response
     * @param accessDeniedException The exception representing access denial
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 🔒 Set an error message to be displayed on the access-denied page
        request.setAttribute("message", "You are not authorized to access this page.");

        // 📄 Forward the request to a custom access-denied view (e.g., Thymeleaf template)
        request.getRequestDispatcher("/access-denied").forward(request, response);
    }
}
