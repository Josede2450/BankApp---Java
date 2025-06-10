package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a verification token used for user email confirmation or password reset.
 */
@Entity
public class VerificationToken {

    /**
     * Primary key - auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique token string sent to the user's email.
     */
    private String token;

    /**
     * One-to-one relationship with the user associated with the token.
     * Each user has a single verification token at any time.
     */
    @OneToOne
    @JoinColumn(nullable = false, name = "user_id") // Foreign key column: user_id
    private Users user;

    /**
     * Expiration date and time of the token.
     * Used to determine if the token is still valid.
     */
    private LocalDateTime expiryDate;

    // ------------------------------------------------------------------------------------
    // ✅ Constructors
    // ------------------------------------------------------------------------------------

    /**
     * Required no-arg constructor for JPA.
     */
    public VerificationToken() {
    }

    /**
     * Constructor for setting token details at creation time.
     *
     * @param token the token string
     * @param user the associated user
     * @param expiryDate expiration date/time of the token
     */
    public VerificationToken(String token, Users user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    // ------------------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
