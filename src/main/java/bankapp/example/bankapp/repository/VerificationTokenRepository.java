package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.Users;
import bankapp.example.bankapp.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing VerificationToken entities.
 * Extends JpaRepository to provide basic CRUD operations.
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Finds a verification token by its token string value.
     *
     * @param token the verification token string
     * @return the corresponding VerificationToken object
     */
    VerificationToken findByToken(String token);

    /**
     * Finds a verification token associated with a specific user.
     *
     * @param user the Users entity
     * @return the associated VerificationToken
     */
    VerificationToken findByUser(Users user);
}
