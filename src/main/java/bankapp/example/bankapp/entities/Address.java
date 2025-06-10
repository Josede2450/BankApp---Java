package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

/**
 * Entity representing a user's address.
 * Shares the same primary key as the associated user (1-to-1 relationship).
 */
@Entity
@Table(name = "address") // Maps to the 'address' table
public class Address {

    // ------------------------------------------------------------------------
    // 📌 Primary Key (shared with Users entity)
    // ------------------------------------------------------------------------

    /**
     * Primary key for Address, which also serves as a foreign key to Users.
     */
    @Id
    private Integer userId;

    // ------------------------------------------------------------------------
    // 📌 Address Fields
    // ------------------------------------------------------------------------

    private String addressType; // e.g., "Home", "Work"
    private String street;
    private String state;
    private String zipCode;
    private String country;

    /**
     * Automatically updated timestamp when address is modified.
     */
    @UpdateTimestamp
    private Timestamp updatedAt;

    // ------------------------------------------------------------------------
    // 📌 Relationship with Users
    // ------------------------------------------------------------------------

    /**
     * One-to-one relationship with Users.
     * Uses the same ID (via @MapsId) so Address.userId = Users.userId.
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id") // Specifies join column
    private Users user;

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
