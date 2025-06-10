package bankapp.example.bankapp.security;

import bankapp.example.bankapp.entities.Users;
import bankapp.example.bankapp.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This service is used to load user-specific data during authentication.
 */
@Service // Marks this as a Spring service component for auto-detection and dependency injection
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Injects the UserRepository to query user data

    /**
     * Loads the user by their email (used as the username in this application).
     *
     * @param email the email address provided at login
     * @return UserDetails object containing user credentials and roles
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 🔍 Attempt to find the user by email
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 🧾 Return a UserDetails object using the Spring Security builder
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())                     // Set email as username
                .password(user.getPassword())                     // Set hashed password
                .authorities(                                     // Convert roles to "ROLE_" prefixed strings
                        user.getRoles().stream()
                                .map(role -> "ROLE_" + role.getRoleName())
                                .toArray(String[]::new)
                )
                .disabled(!user.isEnabled())                      // 🚫 Disable login if user is not enabled
                .build();                                         // ✅ Return fully built UserDetails object
    }
}
