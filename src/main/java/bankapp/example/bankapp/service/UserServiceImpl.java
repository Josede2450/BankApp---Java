package bankapp.example.bankapp.service;

// ===== Imports =====
import bankapp.example.bankapp.entities.Users;
import bankapp.example.bankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service so it can be injected where needed
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository; // Injects the UserRepository to interact with the database

    /**
     * Saves a new user or updates an existing user.
     * Automatically sets the address back-reference if available.
     */
    @Override
    public Users saveUser(Users user) {
        // ✅ Maintain bidirectional relationship with Address (if present)
        if (user.getAddress() != null) {
            user.getAddress().setUser(user); // Ensures address has a back-reference to user
        }

        // ✅ Update existing user if ID exists and is valid
        if (user.getUserId() != null && userRepository.existsById(user.getUserId())) {
            return userRepository.saveAndFlush(user); // Immediately persist and flush updates
        }

        // ✅ Otherwise, create a new user
        return userRepository.save(user);
    }

    /**
     * Returns all users in the system.
     */
    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Searches for users by first name, last name, or email using a case-insensitive query.
     */
    @Override
    public List<Users> searchUsers(String query) {
        return userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query
                );
    }

    /**
     * Retrieves a user by ID or throws a RuntimeException if not found.
     */
    @Override
    public Users getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Retrieves a user by email, or throws an exception used by Spring Security if not found.
     */
    @Override
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by ID, or returns null if not found (alternative version).
     */
    @Override
    public Users findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Another way to retrieve all users. (Redundant with getAllUsers)
     */
    @Override
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    /**
     * Deletes a user from the system based on ID.
     */
    @Override
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Finds a user by email but returns it as an Optional for more flexible handling.
     */
    @Override
    public Optional<Users> findOptionalByEmail(String email) {
        return userRepository.findByEmail(email); // Repo method returns Optional
    }
}
