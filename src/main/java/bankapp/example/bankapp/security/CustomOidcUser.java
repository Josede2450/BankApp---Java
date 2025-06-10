package bankapp.example.bankapp.security;

import bankapp.example.bankapp.entities.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Custom implementation of OidcUser to enrich OIDC-authenticated users
 * with application-specific user details (e.g., roles from the database).
 */
public class CustomOidcUser implements OidcUser {

    private final OidcUser delegate; // The original OIDC user from Spring Security
    private final Users user;        // Our local application user entity

    /**
     * Constructor for wrapping the OIDC user with custom user data.
     *
     * @param delegate The OIDC user from Spring Security
     * @param user The corresponding Users entity from our database
     */
    public CustomOidcUser(OidcUser delegate, Users user) {
        this.delegate = delegate;
        this.user = user;
    }

    /**
     * Returns all attributes provided by the OIDC provider.
     */
    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    /**
     * Returns the authorities/roles granted to the user.
     * These are pulled from the local `Users` entity.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the name/identifier used to represent the user.
     * Here we use the user's email from the local database.
     */
    @Override
    public String getName() {
        return user.getEmail();
    }

    /**
     * Returns OIDC user info (e.g., email, name, locale, etc.).
     */
    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    /**
     * Returns the ID token issued by the identity provider.
     */
    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    /**
     * Returns the full set of claims from the ID token.
     */
    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    /**
     * Exposes the custom Users object to the application.
     */
    public Users getUser() {
        return user;
    }
}
