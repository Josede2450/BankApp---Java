// Service class responsible for sending simple email notifications
package bankapp.example.bankapp.service;

// ===== Spring Framework imports =====
import org.springframework.beans.factory.annotation.Autowired;            // Enables dependency injection
import org.springframework.mail.SimpleMailMessage;                     // Represents a simple text-based email message
import org.springframework.mail.javamail.JavaMailSender;               // Interface for sending emails
import org.springframework.stereotype.Service;                         // Marks this class as a Spring-managed service

// Marks this class as a service bean so it can be injected into controllers or other services
@Service
public class EmailService {

    // Injects the JavaMailSender which is configured via Spring Boot (e.g., in application.properties)
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a plain text email to a specified recipient.
     *
     * @param to      Recipient email address
     * @param subject Subject of the email
     * @param body    Main text content of the email
     */
    public void sendEmail(String to, String subject, String body) {
        // Create a new simple mail message object
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the recipient address
        message.setTo(to);

        // Set the subject line
        message.setSubject(subject);

        // Set the body of the message
        message.setText(body);

        // Send the email using the JavaMailSender instance
        mailSender.send(message);
    }
}
