package bankapp.example.bankapp.entities;

import bankapp.example.bankapp.enums.PaymentType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entity representing a payment made toward a loan.
 * Maps to the 'payment' table in the database.
 */
@Entity
@Table(name = "payment")
public class Payment {

    // ------------------------------------------------------------------------
    // 📌 Primary Key
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private Integer paymentId;

    // ------------------------------------------------------------------------
    // 📌 Relationships
    // ------------------------------------------------------------------------

    /**
     * Many-to-one relationship: many payments can be made to one loan.
     */
    @ManyToOne
    @JoinColumn(name = "loan_id") // Foreign key to loan
    private Loans loan;

    /**
     * Many-to-one relationship: many payments can come from one account.
     */
    @ManyToOne
    @JoinColumn(name = "account_id") // Foreign key to account
    private Account account;

    // ------------------------------------------------------------------------
    // 📌 Payment Details
    // ------------------------------------------------------------------------

    private BigDecimal amount; // Payment amount

    /**
     * Payment type: 'Manual' or 'Auto'.
     * Stored as a string in the database (e.g., "Manual").
     */
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    /**
     * Timestamp of when the payment was made.
     * Defaults to current system time upon creation.
     * This value is immutable (not updatable).
     */
    @Column(nullable = false, updatable = false)
    private Timestamp paymentTime = new Timestamp(System.currentTimeMillis());

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Loans getLoan() {
        return loan;
    }

    public void setLoan(Loans loan) {
        this.loan = loan;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Timestamp getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Timestamp paymentTime) {
        this.paymentTime = paymentTime;
    }
}
