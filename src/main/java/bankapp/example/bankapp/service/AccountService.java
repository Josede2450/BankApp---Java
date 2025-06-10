// This interface defines the contract for account-related operations
package bankapp.example.bankapp.service;

// ===== Import entity classes =====
import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.Users;

import java.util.List;

// Service interface for managing bank accounts
public interface AccountService {

    /**
     * Saves a new or updated account to the database.
     * Used when creating or modifying an account for a user.
     *
     * @param account the Account object to be saved
     * @return the saved Account object (with ID, user, etc.)
     */
    Account saveAccount(Account account);

    /**
     * Retrieves all accounts linked to a specific user by their user ID.
     * Useful for showing a user's list of accounts.
     *
     * @param userId the ID of the user
     * @return a list of Account objects owned by the user
     */
    List<Account> getAccountsByUserId(Integer userId);

    /**
     * Returns the user's primary account.
     * Your implementation decides how the primary account is determined
     * (e.g., first created, marked by flag, etc.)
     *
     * @param userId the ID of the user
     * @return the primary Account object
     */
    Account getPrimaryAccountByUserId(Integer userId);

    /**
     * Finds an account by its unique ID.
     * Useful for fetching details before update or transfer.
     *
     * @param accountId the ID of the account
     * @return the Account object, or null if not found
     */
    Account findById(Integer accountId);

    /**
     * Retrieves all accounts in the system (admin use).
     *
     * @return a list of all Account entities
     */
    List<Account> findAll();

    // Note: Consider adding:
    // - deleteAccount(Integer accountId)
    // - transferFunds(Account from, Account to, BigDecimal amount)
}
