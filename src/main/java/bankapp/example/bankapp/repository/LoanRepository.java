package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.Loans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing Loans entities.
 * Extends JpaRepository to provide standard CRUD and custom query methods.
 */
public interface LoanRepository extends JpaRepository<Loans, Integer> {

    // ------------------------------------------------------------------------------------
    // ✅ Standard methods inherited from JpaRepository:
    // - save(Loans loan)
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)
    // ------------------------------------------------------------------------------------

    /**
     * Finds all loans associated with a given account ID.
     * Spring Data JPA infers this from method naming convention.
     *
     * @param accountId the ID of the account
     * @return list of Loans linked to the account
     */
    List<Loans> findByAccount_AccountId(Integer accountId);

    /**
     * Finds all loans where the autopay account matches the given account ID.
     *
     * @param accountId the autopay account ID
     * @return list of Loans configured for autopay from that account
     */
    List<Loans> findByAutoPayAccount_AccountId(Integer accountId);

    /**
     * Custom JPQL query to fetch all approved loans that have auto-pay enabled,
     * along with their associated payments (eagerly loaded).
     *
     * @return list of approved loans with auto-pay enabled
     */
    @Query("SELECT l FROM Loans l LEFT JOIN FETCH l.payments WHERE l.loanStatus = 'Approved' AND l.autoPayEnabled = true")
    List<Loans> findAllApprovedWithAutoPay();

    /**
     * Custom JPQL query to fetch all approved loans regardless of autopay status.
     *
     * @return list of approved loans
     */
    @Query("SELECT l FROM Loans l WHERE l.loanStatus = 'Approved'")
    List<Loans> findAllApprovedLoans();
}
