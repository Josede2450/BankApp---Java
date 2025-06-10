package bankapp.example.bankapp.service;

// ===== Imports for entity classes =====
import bankapp.example.bankapp.entities.Loans; // Included in case future loan-related user queries are added
import bankapp.example.bankapp.entities.Users;

import java.util.List;
import java.util.Optional;

/**
 * Service interface that defines all business operations related to Users.
 * This is implemented by UserServiceImpl and used in controllers or other services.
 */
public interface UserService {

    /**
     * Creates or updates a user in the system.
     * Used during registration, account linking, or profile update.
     *
     * @param user the user object to be saved or updated
     * @return the saved user with updated database fields (e.g., ID, timestamps)
     */
    Users saveUser(Users user);

    /**
     * Retrieves all users in the system.
     * Typically used in administrative dashboards.
     *
     * @return list of all users
     */
    List<Users> getAllUsers();

    /**
     * Searches users by first name, last name, or email address.
     * Useful for building dynamic search interfaces.
     *
     * @param query the keyword to search by
     * @return list of matching users
     */
    List<Users> searchUsers(String query);

    /**
     * Retrieves a user by their database ID.
     * May be used in profile viewing or account linking.
     *
     * @param id the user's ID
     * @return the user object, or null if not found
     */
    Users getUserById(Integer id);

    /**
     * Retrieves a user by their email address.
     * Commonly used in login authentication and OAuth2 flows.
     *
     * @param username the email of the user
     * @return the matching user, or null if not found
     */
    Users findByEmail(String username);

    /**
     * Same as findByEmail, but returns an Optional for more flexible handling.
     *
     * @param email the email to search for
     * @return an Optional containing the user, or empty if not found
     */
    Optional<Users> findOptionalByEmail(String email);

    /**
     * Another method to find a user by ID. Redundant with getUserById().
     * Consider consolidating these two methods in the future.
     *
     * @param id the user's ID
     * @return the user object
     */
    Users findById(Integer id);

    /**
     * Returns all users from the database (same as getAllUsers).
     * You may consolidate with getAllUsers() for consistency.
     *
     * @return list of users
     */
    List<Users> findAll();

    /**
     * Deletes a user from the database based on their ID.
     * Used for account deletion or admin cleanup.
     *
     * @param userId the ID of the user to be deleted
     */
    void deleteUserById(Integer userId);
}
