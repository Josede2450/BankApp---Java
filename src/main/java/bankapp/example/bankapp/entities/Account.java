package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Entity representing a user's bank account.
 * Maps to the 'account' table in the database.
 */
@Entity
@Table(name = "account")
public class Account {

    // ------------------------------------------------------------------------
    // 📌 Primary Fields
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    private String accountNumber;       // e.g., IBAN or internal format
    private String accountType;         // e.g., "Checking", "Savings"
    private String status = "Active";   // Account status: Active, Closed, Frozen

    private BigDecimal balance;         // Current account balance

    @Column(updatable = false)
    @CreationTimestamp                 // Automatically set on creation
    private Timestamp createdAt;

    @UpdateTimestamp                   // Automatically updated on change
    private Timestamp updatedAt;

    // ------------------------------------------------------------------------
    // 📌 Relationships
    // ------------------------------------------------------------------------

    /**
     * Many-to-one: many accounts can belong to one user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key to Users
    private Users user;

    /**
     * One-to-many: one account can fund multiple loans.
     */
    @OneToMany(mappedBy = "account", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    private List<Loans> loans;

    /**
     * One-to-many: if this account is used for auto-pay in other loans.
     */
    @OneToMany(mappedBy = "autoPayAccount")
    private List<Loans> autoPayLoans;

    /**
     * One-to-many: all transactions linked to this account.
     */
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transactions> transactions;

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<Loans> getLoans() {
        return loans;
    }

    public void setLoans(List<Loans> loans) {
        this.loans = loans;
    }

    public List<Loans> getAutoPayLoans() {
        return autoPayLoans;
    }

    public void setAutoPayLoans(List<Loans> autoPayLoans) {
        this.autoPayLoans = autoPayLoans;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }
}
