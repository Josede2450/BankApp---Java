package bankapp.example.bankapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Main security configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    // Custom service to handle OAuth2 user information (e.g., Google login)
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    // Custom handler for login failure events (e.g., wrong password)
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    // Custom handler for access denied (403) errors
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     * Spring Security uses this to encode and match user passwords securely.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security filter chain configuration.
     * Sets up endpoint access rules, login/logout behavior, and error handling.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 🔐 Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/users/request-account",
                                "/users/save",
                                "/users/create",
                                "/login/oauth2/**",
                                "/users/forgot-password",
                                "/users/reset-password",
                                "/error",
                                "/oauth2/**"
                        ).permitAll() // Allow public access to these endpoints
                        .anyRequest().authenticated() // All other requests require authentication
                )

                // 📝 Form-based login configuration
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .defaultSuccessUrl("/home/page", true) // Redirect after successful login
                        .failureHandler(customAuthenticationFailureHandler) // Custom failure logic
                        .permitAll()
                )

                // 🌐 OAuth2 login configuration (e.g., Google)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Reuse same login page
                        .defaultSuccessUrl("/home/page", true) // Redirect after successful login
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserService) // Handle user info from OAuth provider
                        )
                )

                // 🚪 Logout configuration
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Redirect after logout
                        .permitAll()
                )

                // 🚫 Access denied (403) handling
                .exceptionHandling(exception -> exception
                                .accessDeniedHandler(accessDeniedHandler) // Custom handler for forbidden access
                        // Optional: you can add .authenticationEntryPoint(...) for handling unauthenticated access
                );

        return http.build(); // Finalize the security filter chain
    }
}
