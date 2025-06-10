package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.LateFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing LateFee entities.
 * Extends JpaRepository to provide basic CRUD operations and custom queries.
 */
@Repository
public interface LateFeeRepository extends JpaRepository<LateFee, Integer> {

    /**
     * Retrieves the late fee record associated with a specific loan ID.
     * Uses nested property navigation to resolve: LateFee → Loan → loanId
     *
     * @param loanId the ID of the loan
     * @return the matching LateFee entity, or null if none exists
     */
    LateFee findByLoan_LoanId(Integer loanId);
}
