package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * Represents a system user (client, admin, etc.).
 * Maps to the 'users' table in the database.
 */
@Entity
@Table(name = "users") // Explicitly names the table as 'users'
public class Users {

    // ------------------------------------------------------------------------
    // 📌 Primary Fields
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private Integer userId;

    private String firstName;
    private String lastName;
    private String gender;

    @Column(unique = true) // Ensure no duplicate emails
    private String email;

    private String password;

    @Column(nullable = false)
    private boolean enabled;

    // ------------------------------------------------------------------------
    // 📌 Timestamps (automatically managed)
    // ------------------------------------------------------------------------

    @Column(updatable = false)
    @CreationTimestamp // Auto-filled when the record is created
    private Timestamp createdAt;

    @UpdateTimestamp // Auto-updated whenever the record changes
    private Timestamp updatedAt;

    // ------------------------------------------------------------------------
    // 📌 Relationships
    // ------------------------------------------------------------------------

    /**
     * One-to-one relationship with Address.
     * This side is inverse; 'user' is the owning side in the Address entity.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Address address;

    /**
     * Many-to-many relationship with Roles.
     * Users can have multiple roles (e.g., Client, Admin).
     */
    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles;

    /**
     * One-to-many relationship with Account.
     * One user can have multiple bank accounts.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
