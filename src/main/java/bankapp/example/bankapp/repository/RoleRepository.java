package bankapp.example.bankapp.repository;

import bankapp.example.bankapp.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Roles entities.
 * Extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
public interface RoleRepository extends JpaRepository<Roles, Integer> {

    /**
     * Finds a role entity by its name.
     * Used when assigning roles to users (e.g., "Admin", "Client", etc.).
     *
     * @param roleName the name of the role
     * @return the matching Roles entity
     */
    Roles findByRoleName(String roleName);
}
