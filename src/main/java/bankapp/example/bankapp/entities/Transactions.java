package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entity representing a financial transaction (deposit, withdrawal, transfer, etc.).
 * Maps to the 'transactions' table in the database.
 */
@Entity
@Table(name = "transactions")
public class Transactions {

    // ------------------------------------------------------------------------
    // 📌 Core Fields
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private Integer transactionId;

    private String transactionType; // e.g., "Deposit", "Withdrawal", "Transfer"

    private BigDecimal amount; // Transaction amount

    private String description; // Optional text description or notes

    /**
     * Optional field to link this transaction to a corresponding one
     * (e.g., outgoing & incoming part of a transfer).
     */
    private Integer linkedTransactionId;

    // ------------------------------------------------------------------------
    // 📌 Timestamps
    // ------------------------------------------------------------------------

    @Column(updatable = false)
    @CreationTimestamp // Auto-set on insert
    private Timestamp createdAt;

    @UpdateTimestamp // Auto-set on update
    private Timestamp updatedAt;

    // ------------------------------------------------------------------------
    // 📌 Relationships
    // ------------------------------------------------------------------------

    /**
     * Many-to-one relationship: many transactions can belong to one account.
     */
    @ManyToOne
    @JoinColumn(name = "account_id") // Foreign key column
    private Account account;

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLinkedTransactionId() {
        return linkedTransactionId;
    }

    public void setLinkedTransactionId(Integer linkedTransactionId) {
        this.linkedTransactionId = linkedTransactionId;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
