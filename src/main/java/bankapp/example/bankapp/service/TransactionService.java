// Interface defining the contract for handling transactions between accounts
package bankapp.example.bankapp.service;

// ===== Import entity classes =====
import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.Transactions;

import java.util.List; // For returning lists of transactions

/**
 * Service interface for managing account transactions.
 * Includes operations for transferring funds and retrieving transaction history.
 */
public interface TransactionService {

    /**
     * Retrieves all transactions for a specific user by user ID.
     * Useful for showing the user their transaction history.
     *
     * @param userId the ID of the user
     * @return a list of transaction records linked to the user
     */
    List<Transactions> getTransactionsByUserId(Integer userId);

    /**
     * Transfers funds between two accounts and records the transaction.
     *
     * @param sender   the account sending the funds
     * @param recipient the account receiving the funds
     * @param amount   the amount of money to transfer
     */
    void transferFunds(Account sender, Account recipient, Double amount);

    /**
     * Saves a transaction to the database.
     * Can be used for manual or automated transaction entries.
     *
     * @param txn the transaction to save
     */
    void saveTransaction(Transactions txn);

    /**
     * Retrieves all transactions in the system.
     * Useful for admin views and reporting.
     *
     * @return list of all Transactions
     */
    List<Transactions> findAll();

    /**
     * Finds a specific transaction by its unique ID.
     *
     * @param id the transaction ID
     * @return the transaction entity if found
     */
    Transactions findById(Integer id);

    /**
     * Deletes a transaction from the system by ID.
     * Typically used for admin correction or rollback.
     *
     * @param id the ID of the transaction to delete
     */
    void deleteTransactionById(Integer id);
}
