// Implementation class for managing transactions between bank accounts
package bankapp.example.bankapp.service;

// ===== Import entity and repository classes =====
import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.Transactions;
import bankapp.example.bankapp.repository.AccountRepository;
import bankapp.example.bankapp.repository.TransactionRepository;

import jakarta.transaction.Transactional; // Enables transactional integrity
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// Marks this class as a Spring service component
@Service
public class TransactionServiceImpl implements TransactionService {

    // Injects the transaction repository for DB operations related to transactions
    @Autowired
    private TransactionRepository transactionRepository;

    // Injects the account repository for DB operations related to accounts
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Retrieves all transactions for a specific user.
     * Useful for showing the transaction history of a logged-in user.
     *
     * @param userId the ID of the user
     * @return list of transactions related to the user's accounts
     */
    @Override
    public List<Transactions> getTransactionsByUserId(Integer userId) {
        return transactionRepository.findAllByAccount_User_UserId(userId);
    }

    /**
     * Transfers funds from one account to another with transaction management.
     * The method is marked @Transactional to ensure atomicity.
     *
     * @param sender the account that sends funds
     * @param recipient the account that receives funds
     * @param amount the amount to transfer
     */
    @Override
    @Transactional // All operations here must succeed or will be rolled back together
    public void transferFunds(Account sender, Account recipient, Double amount) {
        // Convert double to BigDecimal for financial accuracy
        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        // Re-fetch the sender and recipient accounts from DB to ensure they're managed by JPA
        Account managedSender = accountRepository.findById(sender.getAccountId()).orElseThrow();
        Account managedRecipient = accountRepository.findById(recipient.getAccountId()).orElseThrow();

        // Deduct the amount from the sender's balance
        managedSender.setBalance(managedSender.getBalance().subtract(transferAmount));

        // Add the amount to the recipient's balance
        managedRecipient.setBalance(managedRecipient.getBalance().add(transferAmount));

        // Save both updated accounts
        accountRepository.save(managedSender);
        accountRepository.save(managedRecipient);

        // Optionally flush to commit changes immediately
        accountRepository.flush();
    }

    /**
     * Persists a transaction record to the database.
     * Can be used independently from fund transfers (e.g., external logs).
     *
     * @param txn the transaction to save
     */
    @Override
    public void saveTransaction(Transactions txn) {
        transactionRepository.save(txn);
    }

    /**
     * Retrieves all transactions in the system.
     * Useful for admins or reporting purposes.
     *
     * @return list of all transactions
     */
    @Override
    public List<Transactions> findAll() {
        return transactionRepository.findAll();
    }

    /**
     * Finds a specific transaction by its ID.
     *
     * @param id the transaction ID
     * @return the matching transaction or null if not found
     */
    @Override
    public Transactions findById(Integer id) {
        return transactionRepository.findById(id).orElse(null);
    }

    /**
     * Deletes a transaction from the database by ID.
     * Typically used in administrative workflows.
     *
     * @param id the transaction ID to delete
     */
    @Override
    public void deleteTransactionById(Integer id) {
        transactionRepository.deleteById(id);
    }
}
