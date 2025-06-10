// Implementation of the AccountService interface that provides business logic for account operations
package bankapp.example.bankapp.service;

// ===== Import entity and repository classes =====
import bankapp.example.bankapp.entities.Account;
import bankapp.example.bankapp.entities.Loans;
import bankapp.example.bankapp.entities.Users;
import bankapp.example.bankapp.repository.AccountRepository;
import bankapp.example.bankapp.repository.LoanRepository;

import jakarta.transaction.Transactional; // (Optional) used to mark transactional boundaries
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.stereotype.Service; // Marks this class as a Spring service

import java.math.BigDecimal;
import java.util.List;

// Marks this class as a Spring-managed service bean
@Service
public class AccountServiceImpl implements AccountService {

    // Injects AccountRepository to perform DB operations on Account entities
    @Autowired
    private AccountRepository accountRepository;

    // Injects LoanRepository (not used in this class directly, but can be for future logic)
    @Autowired
    private LoanRepository loanRepository;

    /**
     * Saves a new account to the database.
     * Also generates a random account number in the format: AC#########.
     */
    @Override
    public Account saveAccount(Account account) {
        // Generate a simple pseudo-random account number (e.g., AC485302194)
        String generatedAccountNumber = "AC" + (int)(Math.random() * 1_000_000_000);

        // Assign the generated account number to the account
        account.setAccountNumber(generatedAccountNumber);

        // Save the account to the database and return the saved entity
        return accountRepository.save(account);
    }

    /**
     * Retrieves all accounts associated with a given user ID.
     */
    @Override
    public List<Account> getAccountsByUserId(Integer userId) {
        // Uses a custom query method defined in AccountRepository
        return accountRepository.findByUserUserId(userId);
    }

    /**
     * Returns the primary account for a given user.
     * In this implementation, it simply returns the first account in the list.
     */
    @Override
    public Account getPrimaryAccountByUserId(Integer userId) {
        // Fetch all accounts for the user
        List<Account> accounts = accountRepository.findByUserUserId(userId);

        // Return the first one if available; otherwise return null
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    /**
     * Retrieves all accounts in the system (admin use).
     */
    @Override
    public List<Account> findAll() {
        // Return all accounts using repository built-in method
        return accountRepository.findAll();
    }

    /**
     * Finds a specific account by its ID.
     * Throws an exception if not found.
     */
    @Override
    public Account findById(Integer accountId) {
        // Use Optional to handle not found case
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
