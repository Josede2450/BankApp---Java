package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Entity representing a loan.
 * Maps to the 'loan' table in the database.
 */
@Entity
@Table(name = "loan")
public class Loans {

    // ------------------------------------------------------------------------
    // 📌 Primary Key
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer loanId;

    // ------------------------------------------------------------------------
    // 📌 Loan Details
    // ------------------------------------------------------------------------

    private String loanType;             // e.g., "Personal", "Mortgage"
    private String loanStatus;           // e.g., "Approved", "Pending"
    private BigDecimal amount;           // Original loan amount
    private String description;          // Optional notes
    private BigDecimal interestRate;     // e.g., 5.5%
    private Integer loanTerm;            // In months
    private BigDecimal remainingAmount;  // Outstanding balance
    private BigDecimal monthlyPayment;   // Calculated monthly due
    private BigDecimal amountPaid = BigDecimal.ZERO; // Total amount paid so far
    private boolean autoPayEnabled = false; // Flag for automatic payment
    private BigDecimal expectedTotal;    // Total expected amount with interest

    // ------------------------------------------------------------------------
    // 📌 Relationships
    // ------------------------------------------------------------------------

    /**
     * One-to-one relationship with LateFee.
     * 'mappedBy' means LateFee owns the relationship.
     */
    @OneToOne(mappedBy = "loan", cascade = CascadeType.ALL)
    private LateFee lateFee;

    /**
     * Many-to-one relationship: many loans can be tied to one primary account.
     */
    @ManyToOne
    @JoinColumn(name = "account_id") // Primary loan account
    private Account account;

    /**
     * Optional autopay account different from main account.
     */
    @ManyToOne
    @JoinColumn(name = "autopay_account_id")
    private Account autoPayAccount;

    /**
     * One-to-many relationship: each loan can have many payments.
     * Orphan removal ensures that deleting a payment here removes it from the database.
     */
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    // ------------------------------------------------------------------------
    // 📌 Timestamps
    // ------------------------------------------------------------------------

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
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

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(Integer loanTerm) {
        this.loanTerm = loanTerm;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public boolean isAutoPayEnabled() {
        return autoPayEnabled;
    }

    public void setAutoPayEnabled(boolean autoPayEnabled) {
        this.autoPayEnabled = autoPayEnabled;
    }

    public BigDecimal getExpectedTotal() {
        return expectedTotal;
    }

    public void setExpectedTotal(BigDecimal expectedTotal) {
        this.expectedTotal = expectedTotal;
    }

    public LateFee getLateFee() {
        return lateFee;
    }

    public void setLateFee(LateFee lateFee) {
        this.lateFee = lateFee;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAutoPayAccount() {
        return autoPayAccount;
    }

    public void setAutoPayAccount(Account autoPayAccount) {
        this.autoPayAccount = autoPayAccount;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
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
}
