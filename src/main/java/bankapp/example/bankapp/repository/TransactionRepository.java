package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Transactions entities.
 * Extends JpaRepository to provide basic CRUD and custom query capabilities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Integer> {

    /**
     * Finds all transactions associated with accounts owned by a specific user.
     * Traverses the entity relationship: Transaction → Account → User
     *
     * @param userId the ID of the user
     * @return a list of all matching transactions
     *
     * Equivalent JPQL:
     * SELECT t FROM Transactions t WHERE t.account.user.userId = :userId
     */
    List<Transactions> findAllByAccount_User_UserId(Integer userId);
}
