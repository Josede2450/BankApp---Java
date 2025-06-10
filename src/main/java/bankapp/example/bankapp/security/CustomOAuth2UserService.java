package bankapp.example.bankapp.security;

import bankapp.example.bankapp.entities.Roles;
import bankapp.example.bankapp.entities.Users;
import bankapp.example.bankapp.repository.RoleRepository;
import bankapp.example.bankapp.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;

/**
 * Custom OIDC user service for processing users authenticated via external providers (e.g., Google).
 * Extends Spring's default OidcUserService to add logic for syncing local user data.
 */
@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Constructor-based dependency injection for user and role repositories.
     */
    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Loads and processes an OIDC user.
     * If the user is logging in for the first time, creates a new entry in the database.
     *
     * @param request The incoming OIDC user request
     * @return A custom OIDC user wrapping both provider data and local database info
     */
    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        // 🔁 Get the OIDC user from the default implementation
        OidcUser oidcUser = super.loadUser(request);

        // 📧 Extract the user's email
        String email = oidcUser.getEmail();

        // 🔍 Try to find the user in the local database, or create one if not found
        Users dbUser = userRepository.findByEmail(email).orElseGet(() -> {
            // ✨ Create new user based on OAuth2 info
            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setFirstName(oidcUser.getGivenName());
            newUser.setLastName(oidcUser.getFamilyName());
            newUser.setGender("Unspecified"); // Default gender
            newUser.setPassword(""); // No password needed for OAuth login
            newUser.setCreatedAt(Timestamp.from(Instant.now()));
            newUser.setUpdatedAt(Timestamp.from(Instant.now()));

            // 🔐 Assign default role "Client"
            Roles role = roleRepository.findByRoleName("Client");
            newUser.setRoles(Collections.singleton(role));

            // 💾 Save and return new user
            return userRepository.save(newUser);
        });

        // 👤 Wrap the Spring user and our local user into a custom OIDC user
        return new CustomOidcUser(oidcUser, dbUser);
    }
}
