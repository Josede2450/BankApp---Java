package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Users entities.
 * Extends JpaRepository to provide standard CRUD operations and custom queries.
 */
@Repository // Marks this interface as a Spring-managed repository component
public interface UserRepository extends JpaRepository<Users, Integer> {

    // ------------------------------------------------------------------------------------
    // ✅ Built-in methods inherited from JpaRepository include:
    // - save(Users user)
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)
    // - existsById(Integer id)
    // - count()
    // ------------------------------------------------------------------------------------

    /**
     * Custom query to fetch a user by email along with their associated roles.
     * Uses JOIN FETCH to eagerly load roles in a single query.
     *
     * @param email the email to search for
     * @return Optional containing the user, or empty if not found
     */
    @Query("SELECT u FROM Users u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<Users> findByEmail(@Param("email") String email);

    /**
     * Flexible search query: Finds users where any of the following fields
     * contain the given text (case-insensitive):
     * - first name
     * - last name
     * - email
     *
     * Useful for user search interfaces or admin panels.
     *
     * @param firstName part of the first name to match
     * @param lastName part of the last name to match
     * @param email part of the email to match
     * @return list of matching Users
     */
    List<Users> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName,
            String lastName,
            String email
    );
}
