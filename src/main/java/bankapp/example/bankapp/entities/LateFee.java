package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entity representing late fee tracking for a loan.
 * Maps to the 'late_fee' table in the database.
 */
@Entity
@Table(name = "late_fee")
public class LateFee {

    // ------------------------------------------------------------------------
    // 📌 Primary Key
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lateFeeId;

    // ------------------------------------------------------------------------
    // 📌 Relationships
    // ------------------------------------------------------------------------

    /**
     * One-to-one relationship with Loans.
     * Each loan can have at most one associated LateFee entry.
     */
    @OneToOne
    @JoinColumn(name = "loan_id", unique = true) // Ensures 1-to-1 mapping
    private Loans loan;

    // ------------------------------------------------------------------------
    // 📌 Fee Configuration & Tracking
    // ------------------------------------------------------------------------

    /**
     * Number of grace period days before a late fee is applied.
     */
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer gracePeriodDays;

    /**
     * Maximum cap for the late fee that can be charged.
     */
    private BigDecimal maxFee;

    /**
     * Total accrued fee not yet paid.
     */
    @Column(nullable = false)
    private BigDecimal totalAccruedFee = BigDecimal.ZERO;

    /**
     * Total amount of late fee paid by the user.
     */
    @Column(nullable = false)
    private BigDecimal totalPaidFee = BigDecimal.ZERO;

    /**
     * Timestamp of the last time this loan was checked for overdue payments.
     * Useful for scheduled processing and avoiding redundant checks.
     */
    private Timestamp lastCheckedAt;

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getLateFeeId() {
        return lateFeeId;
    }

    public void setLateFeeId(Integer lateFeeId) {
        this.lateFeeId = lateFeeId;
    }

    public Loans getLoan() {
        return loan;
    }

    public void setLoan(Loans loan) {
        this.loan = loan;
    }

    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public BigDecimal getMaxFee() {
        return maxFee;
    }

    public void setMaxFee(BigDecimal maxFee) {
        this.maxFee = maxFee;
    }

    public BigDecimal getTotalAccruedFee() {
        return totalAccruedFee;
    }

    public void setTotalAccruedFee(BigDecimal totalAccruedFee) {
        this.totalAccruedFee = totalAccruedFee;
    }

    public BigDecimal getTotalPaidFee() {
        return totalPaidFee;
    }

    public void setTotalPaidFee(BigDecimal totalPaidFee) {
        this.totalPaidFee = totalPaidFee;
    }

    public Timestamp getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(Timestamp lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }
}
