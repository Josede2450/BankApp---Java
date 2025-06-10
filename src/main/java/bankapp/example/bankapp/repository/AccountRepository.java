package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Account entities.
 * Extends JpaRepository to provide built-in CRUD and custom query capabilities.
 */
@Repository // Registers this interface as a Spring-managed data access component
public interface AccountRepository extends JpaRepository<Account, Integer> {

    /**
     * Finds all accounts that belong to a specific user.
     * Uses nested property resolution: Account → User → userId
     *
     * @param userId the ID of the user
     * @return a list of accounts associated with that user
     *
     * JPQL Equivalent:
     * SELECT a FROM Account a WHERE a.user.userId = :userId
     */
    List<Account> findByUserUserId(Integer userId);
}
